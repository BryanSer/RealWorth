/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.RealWorth;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 */
public class Config {

    private static Map<Item, BigDecimal> SellRate = new HashMap<>();
    private static Set<ClassInfo> ClassInfos = new HashSet<>();

    public static BigDecimal getSellRate(Item i) {
        return SellRate.get(i);
    }

    public static Set<Item> getItems() {
        return SellRate.keySet();
    }

    public static ClassInfo getClassInfo(ItemStack is) {
        for (ClassInfo ci : ClassInfos) {
            if (ci.contains(is)) {
                return ci;
            }
        }
        return null;
    }

    public static void init() {
        if (!Data.Plugin.getDataFolder().exists()) {
            Data.Plugin.saveDefaultConfig();
        }
        SellRate.clear();
        ClassInfos.clear();
        FileConfiguration config;
        File f = new File(Data.Plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(f);
        for (String s : config.getStringList("Sell")) {
            System.out.println("Br.RealWorth.Config.init() : "+ s);
            String v[] = s.split("\\|");
            Item i = new Item(v[0]);
            System.out.println("Item: " + i);
            SellRate.put(i, new BigDecimal(v[1], Data.MathC));
        }
        for (String c : config.getStringList("Class")) {
            ClassInfos.add(new ClassInfo(c));
        }
    }
}
