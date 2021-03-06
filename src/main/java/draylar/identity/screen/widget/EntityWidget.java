package draylar.identity.screen.widget;

import draylar.identity.Identity;
import draylar.identity.network.ClientNetworking;
import draylar.identity.screen.IdentityScreen;
import draylar.identity.screen.ScreenUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.Collections;

public class EntityWidget extends AbstractPressableButtonWidget {

    private final LivingEntity entity;
    private final int size;
    private boolean active;
    private boolean starred;
    private final IdentityScreen parent;

    public EntityWidget(float x, float y, float width, float height, LivingEntity entity, IdentityScreen parent, boolean starred, boolean current) {
        super((int) x, (int) y, (int) width, (int) height, new LiteralText("")); // int x, int y, int width, int height, message
        this.entity = entity;
        size = (int) (25 * (1 / (Math.max(entity.getHeight(), entity.getWidth()))));
        entity.setGlowing(true);
        this.parent = parent;
        this.starred = starred;
        this.active = current;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = mouseX >= (double)this.x && mouseX < (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY < (double)(this.y + this.height);

        if(bl) {
            // Update current Identity
            if(button == 0) {
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeIdentifier(Registry.ENTITY_TYPE.getId(entity.getType()));
                ClientSidePacketRegistry.INSTANCE.sendToServer(ClientNetworking.IDENTITY_REQUEST, packet);
                parent.disableAll();
                active = true;
            }

            // Add to favorites
            else if (button == 1) {
                boolean favorite = false;

                if(starred) {
                    starred = false;
                } else {
                    starred = true;
                    favorite = true;
                }

                // Update server with information on favorite
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeIdentifier(Registry.ENTITY_TYPE.getId(entity.getType()));
                packet.writeBoolean(favorite);
                ClientSidePacketRegistry.INSTANCE.sendToServer(ClientNetworking.FAVORITE_UPDATE, packet);

                // TODO: re-sort screen?
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        ScreenUtils.drawEntity(x + this.getWidth() / 2, (int) (y + this.getHeight() * .75f), size, -10, -10, entity, 15728880);

        // Render selected outline
        if(active) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(Identity.id("textures/gui/selected.png"));
            DrawableHelper.drawTexture(matrices, x, y, getWidth(), getHeight(), 0, 0, 48, 32, 48, 32);
        }

        // Render favorite star
        if(starred) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(Identity.id("textures/gui/star.png"));
            DrawableHelper.drawTexture(matrices, x, y, 0, 0, 15, 15, 15, 15);
        }

        // Draw tooltip
//        float x = MouseUtilities.mouseX;
//        float y = MouseUtilities.mouseY;
//
//        if(getX() <= x && getX() + getWidth() >= x) {
//            if(getY() <= y && getY() + getHeight() >= y) {
//                drawTooltip(matrices, provider);
//                renderToolTip();
//            }
//        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onPress() {

    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;

        if(currentScreen != null) {
            currentScreen.renderTooltip(matrices, Collections.singletonList(new TranslatableText(entity.getType().getTranslationKey())), mouseX, mouseY);
        }
    }
}
