/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package test.javafx.stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.util.Util;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Closing the owner window of a showing FileChooser must make
 * showOpenDialog return instead of leaving the nested event loop stuck,
 * and focus must move on to a remaining window.
 */
public class FileChooserOwnerClosedTest {

    private static final CountDownLatch startupLatch = new CountDownLatch(1);

    @BeforeAll
    static void initFX() throws Exception {
        Util.launch(startupLatch, TestApp.class);
    }

    @AfterAll
    static void teardown() {
        Util.shutdown();
    }

    private static Stage createStage(String title) throws Exception {
        return createStage(title, StageStyle.DECORATED);
    }

    private static Stage createTransparentStage(Stage owner) throws Exception {
        return createStage("Overlay", StageStyle.TRANSPARENT, owner);
    }

    private static Stage createStage(String title, StageStyle style) throws Exception {
        return createStage(title, style, null);
    }

    private static Stage createStage(String title, StageStyle style, Stage owner) throws Exception {
        CountDownLatch shownLatch = new CountDownLatch(1);
        AtomicReference<Stage> ref = new AtomicReference<>();
        Platform.runLater(() -> {
            Stage stage = new Stage(style);
            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.setScene(new Scene(new StackPane(new Label(title)), 300, 240));
            stage.setTitle(title);
            stage.setOnShown(e -> shownLatch.countDown());
            ref.set(stage);
            stage.show();
        });
        Util.waitForLatch(shownLatch, 10, "Timeout showing stage " + title);
        return ref.get();
    }

    @Test
    public void fileChooserReturnsWhenOwnerIsClosed() throws Exception {
        Stage owner = createStage("Owner");

        CountDownLatch dialogReturned = new CountDownLatch(1);
        Platform.runLater(() -> {
            new FileChooser().showOpenDialog(owner);
            dialogReturned.countDown();
        });

        // let the dialog open, then close its owner window
        Thread.sleep(2000);
        Platform.runLater(owner::hide);

        assertTrue(dialogReturned.await(10, TimeUnit.SECONDS),
                "FileChooser.showOpenDialog did not return after its owner window was closed");
    }

    @Test
    public void focusReturnsToOwnerOfOwnerWhenOwnerIsClosed() throws Exception {
        Stage mainStage = createStage("Main");
        Stage overlayStage = createTransparentStage(mainStage);
        runOwnerClosedScenario(mainStage, overlayStage);
    }

    @Test
    public void focusReturnsToRemainingWindowWhenOwnerIsClosed() throws Exception {
        Stage mainStage = createStage("Main");
        Stage overlayStage = createTransparentStage(null);
        runOwnerClosedScenario(mainStage, overlayStage);
    }

    private void runOwnerClosedScenario(Stage mainStage, Stage overlayStage) throws Exception {

        // the focus handoff can only be verified if this application is active
        CountDownLatch overlayFocused = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (overlayStage.isFocused()) {
                overlayFocused.countDown();
            } else {
                overlayStage.focusedProperty().addListener((obs, was, is) -> {
                    if (is) {
                        overlayFocused.countDown();
                    }
                });
                overlayStage.toFront();
                overlayStage.requestFocus();
            }
        });
        Assumptions.assumeTrue(overlayFocused.await(5, TimeUnit.SECONDS),
                "Skipping: application could not gain focus");

        CountDownLatch dialogReturned = new CountDownLatch(1);
        Platform.runLater(() -> {
            new FileChooser().showOpenDialog(overlayStage);
            dialogReturned.countDown();
        });

        Thread.sleep(2000);

        CountDownLatch mainFocused = new CountDownLatch(1);
        Platform.runLater(() -> {
            mainStage.focusedProperty().addListener((obs, was, is) -> {
                if (is) {
                    mainFocused.countDown();
                }
            });
            if (mainStage.isFocused()) {
                mainFocused.countDown();
            }
            overlayStage.hide();
        });

        assertTrue(dialogReturned.await(10, TimeUnit.SECONDS),
                "FileChooser.showOpenDialog did not return after its owner window was closed");
        // without proper sheet dismissal no window becomes key, leaving the
        // system menu bar of the remaining window uninstalled
        assertTrue(mainFocused.await(10, TimeUnit.SECONDS),
                "Remaining window did not regain focus after the FileChooser owner was closed");

        Util.runAndWait(mainStage::hide);
    }

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            // keep the toolkit alive while test windows are hidden
            Platform.setImplicitExit(false);
            startupLatch.countDown();
        }
    }
}
