/*
 * This file is part of LCMC written by Rasto Levrinc.
 *
 * Copyright (C) 2014, Rastislav Levrinc.
 *
 * The LCMC is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * The LCMC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LCMC; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package lcmc.gui.resources.crm;

import java.util.Collection;
import java.util.List;
import lcmc.data.AccessMode;
import lcmc.data.Application;
import lcmc.data.Host;
import lcmc.data.vm.VmsXml;
import lcmc.gui.resources.vms.DomainInfo;
import lcmc.utilities.MyMenuItem;
import lcmc.utilities.Tools;
import lcmc.utilities.UpdatableItem;

public class VirtualDomainMenu extends ServiceMenu {
    
    private final VirtualDomainInfo virtualDomainInfo;

    private final DomainInfo domainInfo;

    public VirtualDomainMenu(VirtualDomainInfo virtualDomainInfo) {
        super(virtualDomainInfo);
        this.virtualDomainInfo = virtualDomainInfo;
        domainInfo = virtualDomainInfo.getDomainInfo();
    }

    @Override
    public List<UpdatableItem> getPulldownMenu() {
        final List<UpdatableItem> items = super.getPulldownMenu();
        addVncViewersToTheMenu(items);
        return items;
    }

    /** Adds vnc viewer menu items. */
    private void addVncViewersToTheMenu(final Collection<UpdatableItem> items) {
        if (Tools.getApplication().isTightvnc()) {
            /* tight vnc test menu */
            final UpdatableItem tightvncViewerMenu = new MyMenuItem(
                            "start TIGHT VNC viewer",
                            null,
                            null,
                            new AccessMode(Application.AccessType.RO, false),
                            new AccessMode(Application.AccessType.RO, false)) {

                private static final long serialVersionUID = 1L;

                @Override
                public String enablePredicate() {
                    final VmsXml vxml = virtualDomainInfo.getVMSXML(getRunningOnHost());
                    if (vxml == null || domainInfo == null) {
                        return "VM is not available";
                    }
                    final int remotePort = vxml.getRemotePort(
                                               domainInfo.getName());
                    if (remotePort <= 0) {
                        return "remote port is not greater than 0";
                    }
                    return null;
                }

                @Override
                public void action() {
                    virtualDomainInfo.hidePopup();
                    final DomainInfo vvdi = domainInfo;
                    final VmsXml vxml = virtualDomainInfo.getVMSXML(getRunningOnHost());
                    if (vxml != null && vvdi != null) {
                        final int remotePort = vxml.getRemotePort(
                                                               vvdi.getName());
                        final Host host = vxml.getHost();
                        if (host != null && remotePort > 0) {
                            Tools.startTightVncViewer(host, remotePort);
                        }
                    }
                }
            };
            items.add(tightvncViewerMenu);
        }

        if (Tools.getApplication().isUltravnc()) {
            /* ultra vnc test menu */
            final UpdatableItem ultravncViewerMenu = new MyMenuItem(
                            "start ULTRA VNC viewer",
                            null,
                            null,
                            new AccessMode(Application.AccessType.RO, false),
                            new AccessMode(Application.AccessType.RO, false)) {

                private static final long serialVersionUID = 1L;

                @Override
                public String enablePredicate() {
                    final VmsXml vxml = virtualDomainInfo.getVMSXML(getRunningOnHost());
                    if (vxml == null || domainInfo == null) {
                        return "VM is not available";
                    }
                    final int remotePort = vxml.getRemotePort(
                                               domainInfo.getName());
                    if (remotePort <= 0) {
                        return "remote port is not greater than 0";
                    }
                    return null;
                }

                @Override
                public void action() {
                    virtualDomainInfo.hidePopup();
                    final DomainInfo vvdi = domainInfo;
                    final VmsXml vxml = virtualDomainInfo.getVMSXML(getRunningOnHost());
                    if (vxml != null && vvdi != null) {
                        final int remotePort = vxml.getRemotePort(
                                                           vvdi.getName());
                        final Host host = vxml.getHost();
                        if (host != null && remotePort > 0) {
                            Tools.startUltraVncViewer(host, remotePort);
                        }
                    }
                }
            };
            items.add(ultravncViewerMenu);
        }

        if (Tools.getApplication().isRealvnc()) {
            /* real vnc test menu */
            final UpdatableItem realvncViewerMenu = new MyMenuItem(
                            "start REAL VNC test",
                            null,
                            null,
                            new AccessMode(Application.AccessType.RO, false),
                            new AccessMode(Application.AccessType.RO, false)) {

                private static final long serialVersionUID = 1L;

                @Override
                public String enablePredicate() {
                    final VmsXml vxml = virtualDomainInfo.getVMSXML(getRunningOnHost());
                    if (vxml == null || domainInfo == null) {
                        return "VM is not available";
                    }
                    final int remotePort = vxml.getRemotePort(
                                               domainInfo.getName());
                    if (remotePort <= 0) {
                        return "remote port is not greater than 0";
                    }
                    return null;
                }

                @Override
                public void action() {
                    virtualDomainInfo.hidePopup();
                    final DomainInfo vvdi = domainInfo;
                    final VmsXml vxml = virtualDomainInfo.getVMSXML(getRunningOnHost());
                    if (vxml != null && vvdi != null) {
                        final int remotePort = vxml.getRemotePort(
                                                            vvdi.getName());
                        final Host host = vxml.getHost();
                        if (host != null && remotePort > 0) {
                            Tools.startRealVncViewer(host, remotePort);
                        }
                    }
                }
            };
            items.add(realvncViewerMenu);
        }
    }

    /** Returns the first on which this vm is running. */
    private Host getRunningOnHost() {
        final List<String> nodes = virtualDomainInfo.getRunningOnNodes(Application.RunMode.LIVE);
        if (nodes != null
            && !nodes.isEmpty()) {
            return virtualDomainInfo.getBrowser().getCluster().getHostByName(nodes.get(0));
        }
        return null;
    }
}