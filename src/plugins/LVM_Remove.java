/*
 * This file is part of DRBD Management Console by LINBIT HA-Solutions GmbH
 * written by Rasto Levrinc.
 *
 * Copyright (C) 2009, LINBIT HA-Solutions GmbH.
 *
 * DRBD Management Console is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * DRBD Management Console is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with drbd; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package plugins;

import drbd.gui.SpringUtilities;
import drbd.gui.dialog.ConfigDialog;
import drbd.gui.resources.Info;
import drbd.gui.resources.BlockDevInfo;
import drbd.gui.resources.DrbdResourceInfo;
import drbd.gui.resources.HostDrbdInfo;

import drbd.utilities.Tools;
import drbd.utilities.RemotePlugin;
import drbd.utilities.MyButton;
import drbd.utilities.MyMenu;
import drbd.utilities.MyMenuItem;
import drbd.utilities.UpdatableItem;
import drbd.utilities.Unit;
import drbd.data.ConfigData;
import drbd.data.AccessMode;
import drbd.data.Host;
import drbd.gui.dialog.WizardDialog;
import drbd.gui.GuiComboBox;

import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/**
 * This class implements LVM remove plugin. Note that no anonymous classes are
 * allowed here, because caching wouldn't work.
 *
 * @author Rasto Levrinc
 * @version $Id$
 */
public final class LVM_Remove implements RemotePlugin {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Name of the LVM menu item. */
    private static final String LVM_MENU_ITEM = "LVM";
    /** Name of the remove menu item. */
    private static final String LV_REMOVE_MENU_ITEM = "LV Remove";
    /** Description. */
    private static final String DESCRIPTION = "Manage logical volumes.";
    /** Description LVM remove. */
    private static final String DESCRIPTION_REMOVE = "Remove the LVM volume.";

    /** Private. */
    public LVM_Remove() {
    }

    /** Inits the plugin. */
    @Override public void init() {
        for (final BlockDevInfo bdi
                        : Tools.getGUIData().getAllBlockDevices()) {
            registerInfo(bdi);
        }
    }

    /** Adds the menu items to the specified info object. */
    private void registerInfo(final Info info) {
        if (info instanceof BlockDevInfo) {
            final JMenu menu = info.getMenu();
            if (menu != null) {
                info.addPluginMenuItem(getRemoveLVMItem((BlockDevInfo) info));
                info.addPluginActionMenuItem(getRemoveLVMItem(
                                                      (BlockDevInfo) info));
            }
        }
    }

    /** Adds menu items from the plugin. */
    @Override public void addPluginMenuItems(final Info info,
                                             final List<UpdatableItem> items) {
        if (items != null) {
            if (info instanceof BlockDevInfo) {
                items.add(getRemoveLVMItem((BlockDevInfo) info));
            }
        }
    }

    /** Remove menu. */
    private MyMenuItem getRemoveLVMItem(final BlockDevInfo bdi) {
        final RemoveItem removeMenu =
            new RemoveItem(LV_REMOVE_MENU_ITEM,
                           null,
                           LV_REMOVE_MENU_ITEM,
                           new AccessMode(ConfigData.AccessType.OP, true),
                           new AccessMode(ConfigData.AccessType.OP, true),
                           bdi);
        return removeMenu;
    }

    /** Remove menu item. (can't use anonymous classes). */
    private final class RemoveItem extends MyMenuItem {
        private static final long serialVersionUID = 1L;
        private final BlockDevInfo bdi;

        public RemoveItem(final String text,
                          final ImageIcon icon,
                          final String shortDesc,
                          final AccessMode enableAccessMode,
                          final AccessMode visibleAccessMode,
                          final BlockDevInfo bdi) {
            super(text, icon, shortDesc, enableAccessMode, visibleAccessMode);
            this.bdi = bdi;
        }
        public boolean predicate() {
            return true;
        }

        public boolean visiblePredicate() {
            return bdi.isLVM();
        }

        public String enablePredicate() {
            return null;
        }

        @Override public void action() {
            if (Tools.confirmDialog(
                    "Remove Logical Volume",
                    "Remove logical volume and DESTROY all the data on it?",
                    "Remove",
                    "Cancel")) {
                final boolean ret = bdi.lvRemove(false);
                final Host host = bdi.getHost();
                host.getBrowser().getClusterBrowser().updateHWInfo(host);
            }
        }

    }

    /** Shows dialog with description. */
    @Override public void showDescription() {
        final Description description = new Description();
        description.showDialog();
    }

    /** Description dialog. */
    private final class Description extends ConfigDialog {
        /** Serial version UID. */
        private static final long serialVersionUID = 1L;

        public Description() {
            super();
        }

        protected void initDialog() {
            super.initDialog();
            enableComponents();
        }

        protected String getDialogTitle() {
            return "LVM Remove " + Tools.getRelease();
        }

        protected String getDescription() {
            return "You can now use LVM menu items in the "
                   + "\"Storage (DRBD)\" view.<br><br>" + DESCRIPTION;
        }

        protected JComponent getInputPane() {
            return null;
        }
    }

    /** Return unit objects. */
    private Unit[] getUnits() {
        return new Unit[]{
                   //new Unit("", "", "KiByte", "KiBytes"), /* default unit */
                   new Unit("K", "K", "KiByte", "KiBytes"),
                   new Unit("M", "M", "MiByte", "MiBytes"),
                   new Unit("G",  "G",  "GiByte",      "GiBytes"),
                   new Unit("T",  "T",  "TiByte",      "TiBytes")
       };
    }
}