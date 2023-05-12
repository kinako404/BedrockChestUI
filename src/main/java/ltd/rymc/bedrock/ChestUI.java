package ltd.rymc.bedrock;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ChestUI extends JavaPlugin {

    private static Class<?> keyClass;
    private static Object getKeyObject;
    private static Method getKeyMethod;


    @Override
    public void onEnable() {

        if (!loadReflect()) return;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.OPEN_WINDOW) {
            public void onPacketSending(PacketEvent event) {

                if (!FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId())) return;

                PacketContainer packet = event.getPacket();

                int rowSize = getRow(packet.getModifier().read(1));
                if (rowSize < 0 || rowSize > 6) return;

                StructureModifier<WrappedChatComponent> chatComponents = packet.getChatComponents();
                String json = chatComponents.read(0).getJson();

                if (json.contains("chest.row.")) {
                    String title = "{\"translate\":\"container." + (rowSize < 4 ? "chest" : "chestDouble") + "\"}";
                    chatComponents.write(0, WrappedChatComponent.fromJson(title));
                }

                if (rowSize == 3 || rowSize == 6) return;

                String row = "{\"text\":\"chest.row." + rowSize + "\"}";

                String newJson = addRowToJsonExtra(json, row);
                if (newJson == null) newJson = addRowToJson(json, row);
                if (newJson == null) return;

                chatComponents.write(0, WrappedChatComponent.fromJson(newJson));

            }
        });
    }

    private int getRow(Object object) {
        try {
            Object key = keyClass.cast(getKeyMethod.invoke(getKeyObject, object));
            return key == null ? -1 : translateSize(key.toString());
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return -1;
    }

    private int translateSize(String size) {
        switch (size) {
            case "minecraft:generic_9x1":
                return 1;
            case "minecraft:generic_9x2":
                return 2;
            case "minecraft:generic_9x3":
                return 3;
            case "minecraft:generic_9x4":
                return 4;
            case "minecraft:generic_9x5":
                return 5;
            case "minecraft:generic_9x6":
                return 6;
            default:
                return -1;
        }
    }

    private String addRowToJson(String json, String row) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", "");
            jsonObject.put("extra", new JSONArray(Arrays.asList(new JSONObject(row), new JSONObject(json))));
            return jsonObject.toString();
        } catch (JSONException ignored) {
        }
        return null;
    }

    private String addRowToJsonExtra(String json, String row) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray extraList = jsonObject.getJSONArray("extra");
            extraList.put(new JSONObject(row));
            jsonObject.put("extra", extraList);
            return jsonObject.toString();
        } catch (JSONException ignored) {
        }
        return null;
    }

    private boolean loadReflect() {

        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        getLogger().info("NMS Version: " + version);

        String key = "net.minecraft.resources.MinecraftKey";
        String registry = "net.minecraft.core.IRegistry";

        try {

            keyClass = Class.forName(key);

            getKeyObject = Class.forName(ReflectMap.getRegistries(version)).getField(ReflectMap.getKeyField(version)).get(null);

            getKeyMethod = Class.forName(registry).getMethod(ReflectMap.getKeyMethod(version), Object.class);


        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException |
                 NullPointerException e) {
            getLogger().severe("插件在加载时发生错误,你是否在使用一个不受支持的版本?");
            return false;
        }
        return true;
    }
}
