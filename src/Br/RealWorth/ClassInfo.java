/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.RealWorth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 */
public class ClassInfo {

    private Map<Item, Integer> Items = new HashMap<>();
    
    private int TotalWeight;

    public ClassInfo(String v) {
        for (String s : v.split(",")) {
            String k[] = s.split("\\&");
            Item i = new Item(k[0]);
            Items.put(i, Integer.parseInt(k[1]));
        }
        TotalWeight = Items.values().stream().reduce(0, Integer::sum);
    }

    public int getTotalWeight() {
        return TotalWeight;
    }

    public Map<Item, Integer> getItems() {
        return Items;
    }

    public boolean contains(Item is) {
        return Items.keySet().stream().anyMatch(i -> i.isThis(is));
    }

    public boolean contains(ItemStack is) {
        return Items.keySet().stream().anyMatch(i -> i.isThis(is));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.Items);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClassInfo other = (ClassInfo) obj;
        return Objects.equals(this.Items, other.Items);
    }

}
