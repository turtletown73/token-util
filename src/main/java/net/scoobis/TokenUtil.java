package net.scoobis;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TokenUtil implements ModInitializer {
	public static final String MOD_ID = "token-util";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public MinecraftClient CLIENT = MinecraftClient.getInstance();

	@Override
	public void onInitialize() {
		LOGGER.info("Token Util is here!");

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("token")
				.executes(context -> {
					Path filePath = FabricLoader.getInstance().getConfigDir().resolve("savedtoken.json");
					
					JsonObject account = new JsonObject();
					Gson gson = new Gson();

					account.add("accessToken", gson.toJsonTree(CLIENT.getSession().getAccessToken()));
					account.add("uuid", gson.toJsonTree(CLIENT.getSession().getUuidOrNull()));
					account.add("username", gson.toJsonTree(CLIENT.getSession().getUsername()));

					String fileContents = account.toString();
					File file = filePath.toFile();
					try {
						FileWriter writer = new FileWriter(file);
						writer.write(fileContents);
						writer.close();

						context.getSource().sendFeedback(Text.of("Minecraft Auth has been written to: " + filePath));
						return 1;
					} catch (IOException e) {
						context.getSource().sendFeedback(Text.of("Minecraft Auth has failed to write to: '" + filePath + "' error message: " + e.getMessage()));
						return 1;
					}
				})
			);
		});
	}
}