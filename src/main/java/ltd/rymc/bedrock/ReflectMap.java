package ltd.rymc.bedrock;

import java.util.HashMap;

public class ReflectMap {
    private static final HashMap<String, String> registriesMap = new HashMap<>();
    private static final HashMap<String, String> keyFieldMap = new HashMap<>();
    private static final HashMap<String, String> keyMethodMap = new HashMap<>();

    static {

        registriesMap.put("v1_19_R3", "net.minecraft.core.registries.BuiltInRegistries");
        registriesMap.put("v1_19_R2", "net.minecraft.core.registries.BuiltInRegistries");
        registriesMap.put("v1_19_R1", "net.minecraft.core.IRegistry");
        registriesMap.put("v1_18_R2", "net.minecraft.core.IRegistry");
        registriesMap.put("v1_18_R1", "net.minecraft.core.IRegistry");
        registriesMap.put("v1_17_R1", "net.minecraft.core.IRegistry");

        keyFieldMap.put("v1_19_R3", "r");
        keyFieldMap.put("v1_19_R2", "r");
        keyFieldMap.put("v1_19_R1", "ah");
        keyFieldMap.put("v1_18_R2", "ag");
        keyFieldMap.put("v1_18_R1", "aj");
        keyFieldMap.put("v1_17_R1", "ai");

        keyMethodMap.put("v1_19_R3", "b");
        keyMethodMap.put("v1_19_R2", "b");
        keyMethodMap.put("v1_19_R1", "b");
        keyMethodMap.put("v1_18_R2", "b");
        keyMethodMap.put("v1_18_R1", "b");
        keyMethodMap.put("v1_17_R1", "getKey");

    }

    public static String getRegistries(String version) {
        return registriesMap.get(version);
    }

    public static String getKeyField(String version) {
        return keyFieldMap.get(version);
    }

    public static String getKeyMethod(String version) {
        return keyMethodMap.get(version);
    }
}
