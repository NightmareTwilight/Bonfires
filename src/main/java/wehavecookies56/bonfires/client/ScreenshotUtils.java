package wehavecookies56.bonfires.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wehavecookies56.bonfires.Bonfires;
import wehavecookies56.bonfires.BonfiresConfig;
import wehavecookies56.bonfires.client.gui.BonfireScreen;
import wehavecookies56.bonfires.client.gui.CreateBonfireScreen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScreenshotUtils {

    private static int timerTicks = 0;
    private static boolean timerStarted = false;
    private static String name;
    private static UUID uuid;

    public static boolean isTimerStarted() {
        return timerStarted;
    }

    public static String getFileNameString(String bonfireName, UUID bonfireUUID) {
        String nameNoInvalid = bonfireName.replaceAll("[\\\\/:*?\"<>|]", "_").toLowerCase();
        return nameNoInvalid + "_" + bonfireUUID.toString() + ".png";
    }

    public static void startScreenshotTimer(String bonfireName, UUID bonfireUUID) {
        name = bonfireName;
        uuid = bonfireUUID;
        timerStarted = true;
        Minecraft.getInstance().options.hideGui = true;
    }

    private static void takeScreenshot(String bonfireName, UUID bonfireUUID) {
        Path p = Paths.get(Minecraft.getInstance().gameDirectory.getPath(), "bonfires");
        String fileName = getFileNameString(bonfireName, bonfireUUID);
        File fileToCreate = new File(p.toFile(), fileName);
        try {
            Files.createDirectories(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String nameNoInvalid = bonfireName.replaceAll("[\\\\/:*?\"<>|]", "_").toLowerCase();
        NativeImage image = ScreenShotHelper.takeScreenshot(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height, Minecraft.getInstance().getMainRenderTarget());

        try {
            image.writeToFile(fileToCreate);
            Bonfires.LOGGER.info("Saved bonfire screenshot " + fileName);
        } catch (IOException ioexception) {
            Bonfires.LOGGER.warn("Couldn't save screenshot", (Throwable)ioexception);
        } finally {
            image.close();
        }
        //Screenshot.grab(p.toFile(), nameNoInvalid + "_" + bonfireUUID.toString() + ".png", Minecraft.getInstance().getMainRenderTarget(), (m) -> {});
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getInstance().level != null) {
                if (Minecraft.getInstance().player != null) {
                    if (timerStarted) {
                        timerTicks++;
                    }
                    if (timerTicks >= BonfiresConfig.Client.screenshotWaitTicks) {
                        timerTicks = 0;
                        timerStarted = false;
                        Minecraft.getInstance().options.hideGui = false;
                        takeScreenshot(name, uuid);
                        if (Minecraft.getInstance().screen != null) {
                            if (Minecraft.getInstance().screen instanceof CreateBonfireScreen) {
                                CreateBonfireScreen create = (CreateBonfireScreen) Minecraft.getInstance().screen;
                                create.onClose();
                            }
                            if (Minecraft.getInstance().screen instanceof BonfireScreen) {
                                BonfireScreen bonfire = (BonfireScreen) Minecraft.getInstance().screen;
                                bonfire.loadBonfireScreenshot();
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderOverlays(RenderGameOverlayEvent.Pre event) {
        event.setCanceled(timerStarted);
    }

}
