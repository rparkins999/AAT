package ch.bailu.aat_gtk.view.map.control

import ch.bailu.aat_gtk.solid.GtkMapDirectories
import ch.bailu.aat_gtk.view.UiController
import ch.bailu.aat_gtk.view.menu.provider.MapMenu
import ch.bailu.aat_gtk.view.menu.provider.MapQueryMenu
import ch.bailu.aat_gtk.view.menu.PopupButton
import ch.bailu.aat_gtk.view.menu.provider.LocationMenu
import ch.bailu.aat_gtk.view.menu.SolidMenuButton
import ch.bailu.aat_lib.gpx.GpxInformation
import ch.bailu.aat_lib.gpx.GpxPointNode
import ch.bailu.aat_lib.map.MapContext
import ch.bailu.aat_lib.map.edge.Position
import ch.bailu.aat_lib.map.layer.selector.OnNodeSelectedInterface
import ch.bailu.aat_lib.preferences.StorageInterface
import ch.bailu.aat_lib.preferences.map.SolidLegend
import ch.bailu.aat_lib.preferences.map.SolidMapGrid
import ch.bailu.foc.FocFactory
import ch.bailu.gtk.gtk.Application
import ch.bailu.gtk.gtk.Window

class InfoBar(app: Application,
              private val nodeInfo: NodeInfo,
              uiController: UiController,
              mcontext: MapContext,
              storage: StorageInterface,
              focFactory: FocFactory,
              window: Window): Bar(Position.RIGHT), OnNodeSelectedInterface {

    private val mapDirectories = GtkMapDirectories(storage, focFactory)

    init {
        add(PopupButton(app, MapMenu(uiController, mcontext, mapDirectories, focFactory, window)).apply { setIcon("menu") }.overlay)
        add(SolidMenuButton(app, SolidMapGrid(storage, mcontext.solidKey)).overlay)
        add(SolidMenuButton(app, SolidLegend(storage, mcontext.solidKey)).overlay)
        add(PopupButton(app, MapQueryMenu(uiController)).apply { setIcon("preferences-system-search-symbolic") }.overlay)
        add(PopupButton(app, LocationMenu()).apply { setIcon("location") }.overlay)
    }

    override fun show() {
        super.show()
        nodeInfo.showCenter()
    }

    override fun hide() {
        super.hide()
        nodeInfo.hide()
    }

    override fun onNodeSelected(IID: Int, info: GpxInformation, node: GpxPointNode, index: Int) {
        nodeInfo.displayNode(info, node, index)
    }
}
