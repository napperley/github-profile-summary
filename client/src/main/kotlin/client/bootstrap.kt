package client

import kotlin.browser.window
import org.webscene.client.html.HtmlCreator as html

fun main(args: Array<String>) {
    Controller.setupRateLimit()
    if (window.location.pathname.startsWith("/search")) Controller.loadSearchPage()
    else if (window.location.pathname.startsWith("/user")) Controller.loadUserPage()
    // TODO: Complete this function.
}
