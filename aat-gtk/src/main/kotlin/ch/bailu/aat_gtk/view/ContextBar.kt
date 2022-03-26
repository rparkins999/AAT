package ch.bailu.aat_gtk.view

import ch.bailu.aat_gtk.util.IconMap
import ch.bailu.aat_gtk.view.map.control.Bar
import ch.bailu.aat_gtk.view.solid.UiController
import ch.bailu.aat_lib.dispatcher.OnContentUpdatedInterface
import ch.bailu.aat_lib.gpx.GpxInformation
import ch.bailu.aat_lib.gpx.InfoID
import ch.bailu.gtk.GTK
import ch.bailu.gtk.gtk.*
import ch.bailu.gtk.helper.LabelHelper
import ch.bailu.gtk.type.Str

class ContextBar(contextCallback: UiController) : OnContentUpdatedInterface {
    val revealer = Revealer()

    private val label = Label()
    private var cache = GpxInformation.NULL

    init {

        val vbox = Box(Orientation.VERTICAL,0)
        val hbox = Box(Orientation.HORIZONTAL,0)
        hbox.append(createImageButton("zoom-fit-best-symbolic") { contextCallback.showInMap(cache) } )
        hbox.append(createButton("Map") { contextCallback.showMap() })
        hbox.append(createButton("List") { contextCallback.showInList() })
        hbox.append(createButton("Detail") { contextCallback.showDetail() })
        hbox.append(createButton("Cockpit") { contextCallback.showCockpit() })


        vbox.append(label)
        vbox.append(hbox)
        revealer.child = vbox
        revealer.revealChild = GTK.FALSE
    }


    private fun createButton(label: String, onClicked: Button.OnClicked) : Button {
        val result = Button.newWithLabelButton(Str(label))
        result.onClicked(onClicked)
        setMargin(result, 3)
        return result
    }

    private fun createImageButton(resource: String, onClicked: Button.OnClicked) : Button {
        val result = Button()
        result.onClicked(onClicked)
        result.child = IconMap.getImage(resource, Bar.ICON_SIZE)
        setMargin(result, 3)
        return result
    }

    private fun setMargin(widget: Widget, margin: Int) {
        widget.marginBottom = margin
        widget.marginTop = margin
        widget.marginStart = margin
        widget.marginEnd = margin
    }

    override fun onContentUpdated(iid: Int, info: GpxInformation) {
        if (iid == InfoID.FILEVIEW) {
            cache = info
            LabelHelper.setLabel(label, info.file.name)
        }
    }
}