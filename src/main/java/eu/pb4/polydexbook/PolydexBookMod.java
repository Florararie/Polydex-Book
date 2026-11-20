package eu.pb4.polydexbook;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Opens the Polydex menu for the book made from a plain book and any leaf block.
 *
 * <p>The book stays a vanilla book with a {@code custom_data} marker, so clients do not need this
 * mod or a resource pack. The recipe adds the marker along with the display name.
 */
public final class PolydexBookMod implements ModInitializer {
    public static final String MOD_ID = "polydex_book";
    private static final String BOOK_TAG = "polydex_book";

    @Override
    public void onInitialize() {
        // Right-clicking air and right-clicking a block take different paths in vanilla, so both
        // need to be handled for the book to work wherever the player is looking.
        UseItemCallback.EVENT.register((player, level, hand) -> openFromBook(player, level, hand));
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> openFromBook(player, level, hand));
    }

    private static InteractionResult openFromBook(Player player, Level level, InteractionHand hand) {
        if (!(level instanceof ServerLevel serverLevel)
                || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        if (!isPolydexBook(player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        }

        // This is exactly the command a player would run themselves; it opens the main index.
        serverLevel.getServer().getCommands()
                .performPrefixedCommand(serverPlayer.createCommandSourceStack(), "polydex");
        return InteractionResult.SUCCESS;
    }

    private static boolean isPolydexBook(ItemStack stack) {
        if (!stack.is(Items.BOOK)) {
            return false;
        }

        var data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().contains(BOOK_TAG);
    }
}
