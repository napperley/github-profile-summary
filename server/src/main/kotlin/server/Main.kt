package server

import io.javalin.Javalin
import io.javalin.embeddedserver.jetty.websocket.WsSession
import org.slf4j.LoggerFactory
import server.util.Heroku
import server.util.RateLimitUtil
import java.util.*

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
    val responseType = "text/html"

    createUserApiRoute(app)
    createUserRoute(app, htmlFileUrl)
    createSearchRoute(app, htmlFileUrl, responseType)
}

fun createUserApiRoute(app: Javalin) {
    app.get("/api/user/:user") { ctx ->
        // TODO: Tidy up the "user api" route handler.
        val user = ctx.param("user")!!

        if (UserCtrl.hasStarredRepo(user)) ctx.json(UserCtrl.getUserProfile(ctx.param("user")!!))
        else ctx.status(400)

//        when (UserCtrl.hasStarredRepo(user)) {
//            true -> ctx.json(UserCtrl.getUserProfile(ctx.param("user")!!))
//            false -> ctx.status(400)
//        }
    }
}

fun createUserRoute(app: Javalin, htmlFileUrl: String) {
    app.get("/user/:user") { ctx ->
        // TODO: Tidy up the "user" route handler.
        val user = ctx.param("user")!!
        val htmlTxt = "".javaClass.getResourceAsStream(htmlFileUrl).bufferedReader().readText()

        if (UserCtrl.hasStarredRepo(user)) ctx.html(htmlTxt)
        else ctx.redirect("/search?q=$user")

//        when (UserCtrl.hasStarredRepo(user)) {
//            true -> ctx.renderVelocity("user.vm", model("user", user))
//            false -> ctx.redirect("/search?q=$user")
//        }
    }
}

private fun createSearchRoute(app: Javalin, htmlFileUrl: String, responseType: String) {
    app.get("/search") { ctx ->
        // TODO: Tidy up the "search" route handler.
        val user = ctx.queryParam("q") ?: ""
        val htmlTxt = "".javaClass.getResourceAsStream(htmlFileUrl).bufferedReader().readText()

        ctx.contentType(responseType)
        if (UserCtrl.hasStarredRepo(user)) ctx.redirect("/user/$user")
        else ctx.html(htmlTxt)

//        when (UserCtrl.hasStarredRepo(user)) {
//            true -> ctx.redirect("/user/$user")
//            false -> ctx.renderVelocity("search.vm", model("q", escapeHtml(user)))
//        }
    }
}

private fun reportRemainingRequests(session: WsSession) = object : TimerTask() {
    override fun run() {
        if (session.isOpen) {
            return session.send(UserCtrl.client.remainingRequests.toString())
        }
        this.cancel()
    }
}



