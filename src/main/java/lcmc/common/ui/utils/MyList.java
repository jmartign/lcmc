/*
 * This file is part of DRBD Management Console by LINBIT HA-Solutions GmbH
 * by Rasto Levrinc.
 *
 * Copyright (C) 2009, Rastislav Levrinc
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

package lcmc.common.ui.utils;

import lcmc.logger.Logger;
import lcmc.logger.LoggerFactory;
import lcmc.common.domain.util.Tools;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import javax.swing.JList;
import javax.swing.JToolTip;
import javax.swing.ListModel;

/**
 * A Jlist with updatable tooltips.
 */
public final class MyList<E> extends JList<E> implements ComponentWithTest {
    private static final Logger LOG = LoggerFactory.getLogger(MyList.class);
    private static final GraphicsDevice SCREEN_DEVICE =
                                         GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private JToolTip toolTip;
    /** Robot to move a mouse a little if a tooltip has changed. */
    private final Robot robot;
    private Color toolTipBackground = null;

    public MyList(final ListModel<E> dataModel, final Color bg) {
        super(dataModel);
        toolTip = createToolTip();
        Robot r = null;
        try {
            r = new Robot(SCREEN_DEVICE);
        } catch (final AWTException e) {
            LOG.appWarning("MyList: robot error");
        }
        robot = r;
        setBackground(bg);
    }

    @Override
    public JToolTip createToolTip() {
        if (toolTip != null) {
            toolTip.setComponent(null);
        }
        toolTip = super.createToolTip();
        if (toolTipBackground != null) {
            toolTip.setBackground(toolTipBackground);
        }
        return toolTip;
    }

    @Override
    public void setToolTipBackground(final Color toolTipBackground) {
        this.toolTipBackground = toolTipBackground;
    }

    /** Sets tooltip and wiggles the mouse to refresh it. */
    @Override
    public void setToolTipText(String text) {
        if (text == null) {
            return;
        }
        if (text.isEmpty()) {
            text = " "; /* can't be "" */
        }
        super.setToolTipText(text);
        toolTip.setTipText(text);
        if (toolTip != null && toolTip.isShowing() && robot != null) {
            final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            int xOffset = 0;
            if (devices.length >= 2) {
                /* workaround for dual monitors that are flipped. */
                //TODO: not sure how is it with three monitors
                final int x1 = devices[0].getDefaultConfiguration().getBounds().x;
                final int x2 = devices[1].getDefaultConfiguration().getBounds().x;
                if (x1 > x2) {
                    xOffset = -x1;
                }
            }
            final Point2D p = MouseInfo.getPointerInfo().getLocation();
            robot.mouseMove((int) p.getX() + xOffset - 1, (int) p.getY());
            robot.mouseMove((int) p.getX() + xOffset + 1, (int) p.getY());
            robot.mouseMove((int) p.getX() + xOffset, (int) p.getY());
        }
    }

    /**
     * Returns location of the tooltip, so that it does not cover the menu
     * item.
     */
    @Override
    public Point getToolTipLocation(final MouseEvent event) {
        final Point screenLocation = getLocationOnScreen();
        final Rectangle sBounds = Tools.getScreenBounds(this);
        final Dimension size = toolTip.getPreferredSize();
        if (screenLocation.x + size.width + event.getX() + 5 > sBounds.width) {
            return new Point(event.getX() - size.width - 5, event.getY() + 20);
        }
        return new Point(event.getX() + 5, /* to not cover the pointer. */
                         event.getY() + 20);
    }

    /** Clean up. */
    void cleanup() {
        for (int i = 0; i < getModel().getSize(); i++) {
            final UpdatableItem m = (UpdatableItem) getModel().getElementAt(i);
            m.cleanup();
        }
        for (final MouseListener ml : getMouseListeners()) {
            removeMouseListener(ml);
        }
        for (final KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }
        for (final MouseMotionListener mml : getMouseMotionListeners()) {
            removeMouseMotionListener(mml);
        }
        toolTip.setComponent(null);
     }
}
