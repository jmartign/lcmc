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

package drbd;

import drbd.utilities.Tools;

import drbd.gui.dialog.WizardDialog;
import drbd.gui.dialog.drbdConfig.Start;
import drbd.gui.resources.DrbdInfo;

/**
 * AddDrbdConfigDialog.
 *
 * Show step by step dialogs that add and configure new host.
 *
 * @author Rasto Levrinc
 * @version $Id$
 */
public final class AddDrbdConfigDialog {
    /** Whether the wizard was canceled. */
    private boolean canceled = false;
    /** Drbd resource info object. */
    private final DrbdInfo drbdInfo;

    /** Prepares new <code>AddDrbdConfigDialog</code> object. */
    public AddDrbdConfigDialog(final DrbdInfo drbdInfo) {
        this.drbdInfo = drbdInfo;
    }

    /** Shows step by step dialogs that add and configure new drbd resource. */
    public void showDialogs() {
        //dri.setDialogStarted(true);
        WizardDialog dialog = new Start(null, drbdInfo);
        Tools.getGUIData().expandTerminalSplitPane(0);
        while (true) {
            final WizardDialog newdialog = (WizardDialog) dialog.showDialog();
            if (dialog.isPressedCancelButton()) {
                dialog.cancelDialog();
                canceled = true;
                Tools.getGUIData().expandTerminalSplitPane(1);
                //dri.getBrowser().reloadAllComboBoxes(null);
                //dri.setDialogStarted(false);
                return;
            } else if (dialog.isPressedFinishButton()) {
                break;
            }
            dialog = newdialog;
        }
        //dri.setDialogStarted(false);
        //dri.getBrowser().reloadAllComboBoxes(null);
        Tools.getGUIData().expandTerminalSplitPane(1);
        Tools.getGUIData().getMainFrame().requestFocus();
    }

    /** Returns whether the wizard was canceled. */
    public boolean isCanceled() {
        return canceled;
    }
}
