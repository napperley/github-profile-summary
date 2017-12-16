package client

import org.w3c.dom.HTMLElement
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import org.w3c.fetch.Response
import org.webscene.client.HttpMethod
import org.webscene.client.dom.DomEditor
import org.webscene.client.dom.DomQuery
import org.webscene.client.fetchData
import org.webscene.client.html.HtmlCreator
import kotlin.browser.document
import kotlin.browser.window

internal object Controller {
    private var user: User? = null

    fun loadSearchPage() {
        DomQuery.allElementsByTagName("title").first().textContent =
            "Github Profile Summary - Visualize your GitHub profile"
        DomEditor.replaceElement {
            HtmlCreator.parentElement("div") {
                id = "body-content"
                classes += "content"
                children += createSearchPage()
            }
        }
    }

    fun loadUserPage() {
        val notFound = -1
        val userQuery = document.location?.search ?: ""
        val startPos = userQuery.indexOf(char = '=')
        val username = if (startPos != notFound) userQuery.substring(startPos) else ""

        // TODO: Remove the line below.
        println("Username: $username")
        DomQuery.allElementsByTagName("title").first().textContent = "$username GitHub profile summary"
        fetchUser()
        DomEditor.replaceElement {
            HtmlCreator.parentElement("div") {
                id = "body-content"
                classes += "content"
                children += createUserPage(user!!)
            }
        }
    }

    private fun fetchUser() {
        val userId = document.body?.getAttribute("data-user-id")
        // TODO: Fix me.
        showLoadScreen()

        fetchData(
            url = "/api/user/$userId",
            method = HttpMethod.GET,
            onResponse = ::userResponseHandler,
            onError = ::userErrorHandler
        )
    }

    private fun userErrorHandler(error: Throwable): String? {
        console.error("Cannot fetch user details: ${error.message}")
        user = null
        cancelOrHideLoadScreen()
        document.querySelector(".content")?.insertAdjacentHTML(position = "beforeEnd",
            text = "<h2>Something went wrong ...</h2>" +
                "<p>This usually means the server has been rate-limited. Check the lower right corner.</p>"
        )
        return error.message
    }

    private fun userResponseHandler(response: Response): User? {
        cancelOrHideLoadScreen()
        user = dataToUser(response.body.user)
        // TODO: Display charts.
//        lineChart("quarterCommitCount", response.data)
//        donutChart("langRepoCount", response.data)
//        donutChart("langStarCount", response.data)
//        donutChart("langCommitCount", response.data)
//        donutChart("repoCommitCount", response.data)
//        donutChart("repoStarCount", response.data)
        return user
    }

    private fun dataToUser(data: dynamic): User {
        val publicRepos = if (data.publicRepos is Int) data.publicRepos.unsafeCast<Int>() else 0

        // TODO: Fill out the User model.
        return User(
            login = "${data.login}",
            fullName = "${data.name ?: ""}",
            createdAt = "${data.createdAt}",
            email = "${data.email}",
            company = "${data.company ?: ""}",
            profileUrl = "${data.htmlUrl}",
            publicRepos = publicRepos
        )
    }

    private fun showLoadScreen() {
        val loadScreen = DomQuery.allElementsByClassNames("load-screen").first()

        // TODO: Fix me.
        if (loadScreen is HTMLElement) loadScreen.style.opacity = "1"
        loadScreen.insertAdjacentHTML("beforeEnd", "<h3>This could take some time ...</h3>")
//        loadScreen.insertAdjacentHTML("beforeEnd", "<h3>This user has a lot of repos!</h3>")
    }

    private fun cancelOrHideLoadScreen() {
        val loadScreen = DomQuery.allElementsByClassNames("load-screen").first()

        // TODO: Fix me.
        if (loadScreen is HTMLElement) loadScreen.style.display = "none"
    }

    fun setupRateLimit() {
        val wsProtocol = if (window.location.protocol.indexOf("https") > -1) "wss" else "ws"
        val url = "$wsProtocol://${window.location.hostname}:${window.location.port}/rate-limit-status"
        val ws = WebSocket(url)

        ws.onmessage = { evt ->
            if (evt.data == "0") {
                DomQuery.allElementsByClassNames("rate-limit").first().classList.add("rate-limited")
            } else {
                DomQuery.allElementsByClassNames("rate-limit").first().classList.remove(
                    "rate-limited")
            }
            DomQuery.elementById("rate-limit-count")?.innerHTML = evt.data
            Unit
        }
    }

    private val Event.data
        get() = "${asDynamic().data}"
}