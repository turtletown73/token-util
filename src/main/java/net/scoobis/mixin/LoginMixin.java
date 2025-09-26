package net.scoobis.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.main.Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Mixin(Main.class)
public class LoginMixin {
    @ModifyVariable(method = "main", at = @At("HEAD"), argsOnly = true, remap = false)
    private static String[] modifyArgs(String[] args) {
        try {
            Scanner scanner = new Scanner(FabricLoader.getInstance().getConfigDir().resolve("newtoken.json").toFile());
            String configStr = scanner.nextLine();
            scanner.close();

            JsonObject configJson = (JsonObject) JsonParser.parseString(configStr);
            String accessToken = configJson.get("accessToken").getAsString();
            String uuid = configJson.get("uuid").getAsString();
            String username = configJson.get("username").getAsString();

            Set<String> optionBlackList = new HashSet<>(Arrays.asList("--accessToken", "--uuid", "--username", "--userType", "--userProperties"));
            List<String> output = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (optionBlackList.contains(arg)) {
                    i++;
                } else {
                    output.add(arg);
                }
            }

            output.add("--accessToken=" + accessToken);
            output.add("--uuid=" + uuid);
            output.add("--username=" + username);
            output.add("--userType=msa");
            output.add("--userProperties={}");

            return output.toArray(new String[0]);
        } catch (FileNotFoundException e) {
            return args;
        }
    }
}
