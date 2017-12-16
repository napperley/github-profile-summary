package client

import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import org.webscene.client.dom.DomEditor
import org.webscene.client.dom.DomQuery
import org.webscene.client.html.HtmlCreator as html
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    setupRateLimit()
    if (window.location.pathname.startsWith("/search")) loadSearchPage()
    // TODO: Complete this function.
}

private fun loadSearchPage() {
    DomEditor.replaceElement {
        html.parentElement("div") {
            id = "body-content"
            classes += "content"
            children += createSearchPage()
        }
    }
}

private fun setupRateLimit() {
    val wsProtocol = if (window.location.protocol.indexOf("https") > -1) "wss" else "ws"
    val url = "$wsProtocol://${window.location.hostname}:${window.location.port}/rate-limit-status"
    val ws = WebSocket(url)

    ws.onmessage = { evt ->
        if (evt.data == "0") {
            document.querySelector(".rate-limit")?.classList?.add("rate-limited")
        } else {
            document.querySelector(".rate-limit")?.classList?.remove("rate-limited")
        }
        DomQuery.elementById("rate-limit-count")?.innerHTML = evt.data
        Unit
    }
}

private val Event.data
    get() = "${asDynamic().data}"
