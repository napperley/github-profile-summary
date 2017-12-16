package server

import io.javalin.Javalin
import io.javalin.embeddedserver.jetty.websocket.WsSession
import org.slf4j.LoggerFactory
import server.util.Heroku
import server.util.RateLimitUtil
import java.util.*

private const val HTML_MIME_TYPE = "text/html"

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("server.MainKt")
    val app = Javalin.create().apply {
        port(Heroku.getPort() ?: 7070)
        enableStaticFiles("/static")
        enableStandardRequestLogging()
        enableDynamicGzip()
    }

    setupRoutes(app)
    app.error(404) { ctx -> ctx.redirect("/search") }
    app.exception(Exception::class.java) { e, ctx ->
        log.warn("Uncaught exception", e)
        ctx.status(500)
    }
    app.ws("/rate-limit-status") { ws ->
        ws.onConnect { session -> Timer().scheduleAtFixedRate(reportRemainingRequests(session), 0, 1000) }
    }

    RateLimitUtil.enableTerribleRateLimiting(app)
    Heroku.enableSslRedirect(app)
    app.start()
}

private fun setupRoutes(app: Javalin) {
    val htmlFileUrl = "/static/page.html"

    createUserApiRoute(app)
    createUserRoute(app = app, htmlFileUrl = htmlFileUrl)
    createSearchRoute(app = app, htmlFileUrl = htmlFileUrl)
}

fun createUserApiRoute(app: Javalin) {
    app.get("/api/user/:user") { ctx ->
        val user = ctx.param("user")!!
        val clientError = 400

        if (UserCtrl.hasStarredRepo(user)) ctx.json(UserCtrl.getUserProfile(ctx.param("user")!!))
        else ctx.status(clientError)
    }
}

fun createUserRoute(app: Javalin, htmlFileUrl: String) {
    app.get("/user/:user") { ctx ->
        val user = ctx.param("user")!!
        val htmlTxt = "".javaClass.getResourceAsStream(htmlFileUrl).bufferedReader().readText()

        if (UserCtrl.hasStarredRepo(user)) {
            ctx.contentType(HTML_MIME_TYPE)
            ctx.html(htmlTxt)
        } else {
            ctx.redirect("/search?q=$user")
        }
    }
}

private fun createSearchRoute(app: Javalin, htmlFileUrl: String) {
    app.get("/search") { ctx ->
        val user = ctx.queryParam("q") ?: ""
        val htmlTxt = "".javaClass.getResourceAsStream(htmlFileUrl).bufferedReader().readText()

        if (UserCtrl.hasStarredRepo(user)) {
            ctx.redirect("/user/$user")
        } else {
            ctx.contentType(HTML_MIME_TYPE)
            ctx.html(htmlTxt)
        }
    }
}

private fun reportRemainingRequests(session: WsSession) = object : TimerTask() {
    override fun run() {
        if (session.isOpen) return session.send("${UserCtrl.client.remainingRequests}")
        cancel()
    }
}



