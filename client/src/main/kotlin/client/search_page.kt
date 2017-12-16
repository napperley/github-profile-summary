package client

import org.webscene.client.HttpMethod
import org.webscene.client.html.InputType
import org.webscene.client.html.ParentHtmlTag
import kotlin.browser.document
import org.webscene.client.html.HtmlCreator as html

internal fun createSearchPage(): ParentHtmlTag = createMainLayout()

private fun createMainLayout() = html.parentElement("div") {
    val query = document.location?.search
    val emptyQuery = "?q=\"\""

    println("Query: $query")
    classes += "search-screen"
    parentHtmlElement("h1") { +"Enter GitHub username" }
    children += createForm()
    if (query == "" || query == emptyQuery) {
        parentHtmlElement("p") { +"Press enter to search." }
    } else {
        children += createSecondaryHeader()
        children += createParagraph()
    }
}

private fun createSecondaryHeader() = html.parentElement("h4") {
    parentHtmlElement("span") { +"Couldn't build profile for " }
    parentHtmlElement("span") {
        classes += "search-term"
        +"\$q"
    }
}

private fun createParagraph() = html.parentElement("p") {
    parentHtmlElement("span") { +"If you are " }
    parentHtmlElement("span") {
        classes += "search-term"
        +"\$q"
    }
    parentHtmlElement("span") { +", please " }
    parentHtmlElement("a") { +"Star the repo" }
    parentHtmlElement("span") { +" and try again." }
    htmlElement("br") {}
    htmlElement("br") {}
    parentHtmlElement("span") {
        +"This is necessary because GitHub has a 5000req/hour rate-limit which would be "
    }
    htmlElement("br") {}
    parentHtmlElement("span") {
        +"reached very quickly if you tried to analyze some of the bigger profiles on GitHub."
    }
    htmlElement("br") {}
    htmlElement("br") {}
    parentHtmlElement("span") {
        +"If the server has been rate-limited (check lower right corner), please come back later"
    }
    htmlElement("br") {}
    parentHtmlElement("span") {
        +"or build the server locally and use your own token"
    }
}

private fun createForm() = html.form(action = "", method = HttpMethod.GET) {
    children += html.input(type = InputType.TEXT, name = "q", autoFocus = true) {
        attributes["placeholder"] = "Example: 'tipsy'"
    }
}