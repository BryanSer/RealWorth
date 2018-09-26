/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.RealWorth;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 */
public class Data {

    public static Main Plugin;

    public static MathContext MathC = MathContext.DECIMAL128;

    private static Map<Item, BigDecimal> DeltaPrice = new HashMap<>();

    private static BigDecimal TotalPrice = null;

    public static BigDecimal getDeltaPrice(Item i) {
        BigDecimal bd = DeltaPrice.get(i);
        return bd == null ? BigDecimal.ZERO : bd;
    }

    public static void init() {
        File f = new File(Plugin.getDataFolder(), "DeltaPrice.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
            for (String s : config.getStringList("Save")) {
                String t[] = s.split("\\|");
                Item i = new Item(t[0]);
                BigDecimal bd = new BigDecimal(t[1], MathC);
                System.out.println("Item: " + i + ", Rate:" + bd.toString());
                DeltaPrice.put(i, bd);
            }
//            for (String key : config.getKeys(false)) {
//                DeltaPrice.put(new Item(key), new BigDecimal(config.getString(key), MathC));
//            }
        }
        TotalPrice = BigDecimal.ZERO;
        for (Item item : Config.getItems()) {
            if (!DeltaPrice.containsKey(item)) {
                DeltaPrice.put(item, BigDecimal.ZERO.abs(MathC));
            }
            try {
                BigDecimal bd = Tools
                        .getBasePrice(item.toItemStack())
                        .add(DeltaPrice.get(item), MathC);
                TotalPrice = TotalPrice.add(bd, MathC);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public static void ModifyDeltaPrice(Item i, BigDecimal price) {
        BigDecimal get = DeltaPrice.get(i);
        if (get != null) {
//            if (price.doubleValue() < 0) {
//                BigDecimal tp = Tools.getPrice(i);
//                if (tp.doubleValue() < price.abs().doubleValue()) {
//                    price = tp.multiply(new BigDecimal("0.1")).negate();
//                }
//            }
            DeltaPrice.put(i, get.add(price, MathC));
        } else {
            DeltaPrice.put(i, price);
        }
        TotalPrice = TotalPrice.add(price, MathC);
    }

    public static BigDecimal getTotalPrice() {
        if (TotalPrice == null) {
            TotalPrice = BigDecimal.ZERO;
            for (Item item : Config.getItems()) {
                if (!DeltaPrice.containsKey(item)) {
                    DeltaPrice.put(item, BigDecimal.ZERO.abs(MathC));
                }
                BigDecimal bd = Tools.getBasePrice(item.toItemStack()).add(getDeltaPrice(item), MathC);
                TotalPrice = TotalPrice.add(bd, MathC);
            }
        }
        return TotalPrice;
    }

    public static void save() {
        File f = new File(Plugin.getDataFolder(), "DeltaPrice.yml");
        YamlConfiguration config = new YamlConfiguration();
        List<String> save = new ArrayList<>();
        for (Map.Entry<Item, BigDecimal> e : DeltaPrice.entrySet()) {
            Item key = e.getKey();
            BigDecimal value = e.getValue();
            //config.set(key.getOrigin(), String.format("%f", value));
            save.add(String.format("%s|%f", key.getOrigin(), value));
        }
        config.set("Save", save);
        try {
            config.save(f);
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
