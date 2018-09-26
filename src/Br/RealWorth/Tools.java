/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.RealWorth;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Worth;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 */
public class Tools {

    private static Map<Item, Item> MatchMap = new HashMap<>();

    private static class FakeItem extends Item {
    }

    public static Item match(ItemStack is) {
        Item i = new Item(is);
        Item o = MatchMap.get(i);
        if (o == null) {
            Item.MatchType mt = Item.MatchType.NotSame;
            F:
            for (Item item : Config.getItems()) {
                switch (item.match(i)) {
                    case NotSame:
                        continue;
                    case RealSame:
                        mt = Item.MatchType.RealSame;
                        o = item;
                        break F;
                    case Same:
                        mt = Item.MatchType.Same;
                        o = item;
                        break;
                }
            }
            if (mt == Item.MatchType.NotSame) {
                o = new FakeItem();
            }
            MatchMap.put(i, o);
        }
        if (o instanceof FakeItem) {
            return null;
        }
        return o;
    }

    public static Worth getWorth() {
        Essentials plugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        return plugin.getWorth();
    }

    public static BigDecimal getBasePrice(ItemStack is) {
        return getWorth().getPrice(is);
    }

    public static BigDecimal getSellRate(ItemStack is) {
        Item i = Tools.match(is);
        if (i != null) {
            return Config.getSellRate(i);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal getPrice(Item is) {
        BigDecimal base = getBasePrice(is.toItemStack());
        if (base != null) {
            base = base.add(Data.getDeltaPrice(is), Data.MathC);
        }
        return base;
    }

    public static BigDecimal getPrice(ItemStack is) {
        BigDecimal base = getBasePrice(is);
        if (base != null) {
            Item i = Tools.match(is);
            if (i != null) {
                base = base.add(Data.getDeltaPrice(i), Data.MathC);
            }
        }
//        if(base.doubleValue() < 0){
//            base = BigDecimal.ZERO;
//        }
        return base;
    }

    public static BigDecimal sell(ItemStack is) {
        BigDecimal price = Tools.getPrice(is);
        if (price == null) {
            return null;
        }
        BigDecimal rate = Tools.getSellRate(is);
        BigDecimal result = null;
        if (rate.equals(BigDecimal.ZERO)) {
            result = price.multiply(new BigDecimal(is.getAmount()), Data.MathC);
        } else {
            if (is.getAmount() != 1) {
                BigDecimal up = BigDecimal.ONE.add(BigDecimal.ONE.add(rate.negate(Data.MathC), Data.MathC).pow(is.getAmount(), Data.MathC).negate(), Data.MathC);
                result = price.multiply(up.divide(rate, Data.MathC), Data.MathC);
            } else {
                result = price;
            }

            Item match = Tools.match(is);
            ClassInfo info = Config.getClassInfo(is);
            if (info == null) {
                Data.ModifyDeltaPrice(match, result.multiply(rate, Data.MathC).negate());
            } else {
                int tw = info.getTotalWeight();
                for (Map.Entry<Item, Integer> e : info.getItems().entrySet()) {
                    Item key = e.getKey();
                    Integer value = e.getValue();
                    Data.ModifyDeltaPrice(key, result.multiply(rate, Data.MathC).multiply(new BigDecimal(value, Data.MathC).divide(new BigDecimal(tw), Data.MathC)).negate());
                }
            }
            for (Item item : Config.getItems()) {
                if (match == item) {
                    continue;
                }
                if (info != null && info.contains(item)) {
                    continue;
                }
                BigDecimal t_p = Tools.getPrice(item);
                if (t_p == null) {
                    continue;
                }
                BigDecimal totalPrice = Data.getTotalPrice();
                BigDecimal weight = t_p.divide(totalPrice, Data.MathC);
                Data.ModifyDeltaPrice(item, weight.multiply(result.multiply(rate, Data.MathC), Data.MathC));
            }
        }
        return result;
    }

}
