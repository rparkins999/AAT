package ch.bailu.aat_gtk.view.map.control

import ch.bailu.aat_gtk.app.GtkAppContext
import ch.bailu.aat_gtk.config.Layout
import ch.bailu.aat_gtk.config.Strings
import ch.bailu.aat_gtk.lib.extensions.margin
import ch.bailu.aat_gtk.lib.extensions.setMarkup
import ch.bailu.aat_lib.gpx.GpxInformation
import ch.bailu.aat_lib.gpx.GpxPointNode
import ch.bailu.aat_lib.html.MarkupBuilderGpx
import ch.bailu.gtk.GTK
import ch.bailu.gtk.gtk.*
import ch.bailu.gtk.type.Str

class NodeInfo {
    private val markupBuilder = MarkupBuilderGpx(GtkAppContext.storage, PangoMarkupConfig)

    private val label = Label(Str.NULL).apply {
        margin(Layout.margin)
    }

    private val scrolled = ScrolledWindow().apply {
        child = label
        setSizeRequest(Layout.windowWidth-Layout.barSize*2-Layout.margin*2,Layout.windowHeight/5)
    }

    val box = Box(Orientation.VERTICAL,0).apply {
        addCssClass(Strings.mapControl)
        margin(Layout.margin)
        append(scrolled)
        valign = Align.START
        halign = Align.CENTER
        visible = GTK.FALSE
    }

    fun showCenter() {
        box.halign = Align.CENTER
        box.visible = GTK.TRUE
    }

    fun showLeft() {
        box.halign = Align.START
        box.visible = GTK.TRUE
    }

    fun showRight() {
        box.halign = Align.END
        box.visible = GTK.TRUE
    }

    fun hide() {
        box.visible = GTK.FALSE
    }

    fun displayNode(info: GpxInformation, node: GpxPointNode, index: Int) {
        markupBuilder.appendInfo(info, index)
        markupBuilder.appendNode(node, info)
        markupBuilder.appendAttributes(node.attributes)

        label.setMarkup(markupBuilder.toString())
        markupBuilder.clear()
    }
}
