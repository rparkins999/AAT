package ch.bailu.aat_gtk.view.list

import ch.bailu.aat_gtk.app.GtkAppContext
import ch.bailu.aat_gtk.lib.menu.MenuFacade
import ch.bailu.aat_gtk.lib.menu.MenuModelBuilder
import ch.bailu.aat_gtk.view.UiController
import ch.bailu.aat_gtk.view.util.margin
import ch.bailu.aat_gtk.view.util.setLabel
import ch.bailu.aat_lib.description.AverageSpeedDescription
import ch.bailu.aat_lib.description.DateDescription
import ch.bailu.aat_lib.description.DistanceDescription
import ch.bailu.aat_lib.description.TimeDescription
import ch.bailu.aat_lib.dispatcher.CustomFileSource
import ch.bailu.aat_lib.dispatcher.DispatcherInterface
import ch.bailu.aat_lib.gpx.InfoID
import ch.bailu.aat_lib.logger.AppLog
import ch.bailu.aat_lib.preferences.SolidDirectoryQuery
import ch.bailu.aat_lib.preferences.map.SolidOverlayFileList
import ch.bailu.aat_lib.preferences.presets.SolidPreset
import ch.bailu.aat_lib.resources.Res
import ch.bailu.aat_lib.resources.ToDo
import ch.bailu.aat_lib.service.directory.IteratorSimple
import ch.bailu.gtk.GTK
import ch.bailu.gtk.bridge.ListIndex
import ch.bailu.gtk.gio.Menu
import ch.bailu.gtk.gtk.*
import ch.bailu.gtk.type.Str

class FileList(app: Application, private val uiController: UiController, dispatcher: DispatcherInterface) {
    val vbox = Box(Orientation.VERTICAL, 12)
    private val listIndex = ListIndex()

    private val dateDescription = DateDescription()
    private val distanceDescription = DistanceDescription(GtkAppContext.storage)
    private val averageSpeedDistanceDescription = AverageSpeedDescription(GtkAppContext.storage)
    private val timeDescription = TimeDescription()
    private val iteratorSimple = IteratorSimple(GtkAppContext)

    private val menuModel = createMenu(app)
    private val customFileSource = CustomFileSource(GtkAppContext)
    private val sdirectory = SolidDirectoryQuery(GtkAppContext.storage, GtkAppContext)

    private var menuIndex = 0
    companion object {
        private val ID_OVERLAYS = Str("id-overlays")
    }

    init {

        try {


            dispatcher.addSource(customFileSource)

            val directory =
                SolidPreset(GtkAppContext.storage).getDirectory(GtkAppContext.dataDirectory)
            sdirectory.setValue(directory.path)

            GtkAppContext.services.directoryService.rescan()

            listIndex.size = iteratorSimple.count

            iteratorSimple.setOnCursorChangedLinsener {
                listIndex.size = iteratorSimple.count
            }

            val factory = SignalListItemFactory()
            factory.onSetup { item: ListItem -> item.child = createItem() }
            factory.onBind { item: ListItem -> bindItem(item, ListIndex.toIndex(item)) }

            val list = ListView(listIndex.inSelectionModel(), factory)
            list.onActivate {
                sdirectory.position.value = it
                customFileSource.setFileID(iteratorSimple.info.file.toString())
            }

            val scrolled = ScrolledWindow()
            scrolled.child = list
            scrolled.hexpand = GTK.TRUE
            scrolled.vexpand = GTK.TRUE

            vbox.append(scrolled)
        } catch (e: Exception) {
            AppLog.e(this, e)
        }
    }


    private fun createItem(): Widget {
        val hbox = Box(Orientation.HORIZONTAL, 5)
        val vbox = Box(Orientation.VERTICAL, 5)
        val menu = MenuButton()

        val label4 = createLabel()
        label4.hide()

        menu.margin(10)
        menu.menuModel = menuModel
        val popover = PopoverMenu(menu.popover.cast())
        popover.addChild(createOverlaysWidget(), ID_OVERLAYS)
        menu.popover.onShow { menuIndex = label4.label.toString().toInt() }

        vbox.hexpand = GTK.TRUE
        hbox.append(vbox)
        hbox.append(menu)
        vbox.margin(5)

        val title = createLabel()
        title.useMarkup = GTK.TRUE
        vbox.append(title)
        vbox.append(createLabel())
        vbox.append(createLabel())
        vbox.append(label4)


        return hbox
    }

    private fun createOverlaysWidget(): Widget {
        val soverlay = SolidOverlayFileList(GtkAppContext.storage, GtkAppContext)
        val list = ListBox()

        soverlay.stringArray.forEachIndexed { index, name ->
            val row = createOverlayRow(index, name)
            list.append(row)
        }
        return list
    }

    private fun createOverlayRow(index: Int, name: String): Widget {
        val row   = Box(Orientation.HORIZONTAL, 2)
        val check = CheckButton()
        val label = Label(Str.NULL)

        label.setLabel("${index+1}: ${name}")
        label.xalign = 0f
        row.margin(5)

        row.append(check)
        row.append(label)
        return row
    }

    private fun createLabel(): Label {
        val result = Label(Str(""))
        result.wrap = GTK.TRUE
        result.xalign = 0f
        result.widthChars = 7
        result.marginEnd = 10
        return result
    }

    private fun bindItem(item: ListItem, index: Int) {
        val hbox = Box(item.child.cast())
        val vbox = Box(hbox.firstChild.cast())

        val label1 = Label(vbox.firstChild.cast())
        val label2 = Label(label1.nextSibling.cast())
        val label3 = Label(label2.nextSibling.cast())
        val label4 = Label(label3.nextSibling.cast())

        iteratorSimple.moveToPosition(index)

        timeDescription.onContentUpdated(InfoID.ALL, iteratorSimple.info)
        dateDescription.onContentUpdated(InfoID.ALL, iteratorSimple.info)
        distanceDescription.onContentUpdated(InfoID.ALL, iteratorSimple.info)
        averageSpeedDistanceDescription.onContentUpdated(InfoID.ALL, iteratorSimple.info)
        dateDescription.onContentUpdated(InfoID.ALL, iteratorSimple.info)

        label1.setLabel("<b>${dateDescription.valueAsString}</b>")
        label2.setLabel("${distanceDescription.valueAsString} - ${averageSpeedDistanceDescription.valueAsString} - ${timeDescription.valueAsString}")
        label3.setLabel(iteratorSimple.info.file.name)
        label4.setLabel(index.toString())
    }

    private fun createMenu(app: Application): Menu {
        val menuFacade = MenuFacade(app)
        menuFacade.build()
            .submenu(Res.str().file_overlay(), MenuModelBuilder().custom(ID_OVERLAYS.toString()))
            .label(ToDo.translate("Load...")) {
                sdirectory.position.value = menuIndex
                iteratorSimple.moveToPosition(menuIndex)
                customFileSource.setFileID(iteratorSimple.info.file.toString())
                uiController.showContextBar()
            }

        return menuFacade.model
    }

}
