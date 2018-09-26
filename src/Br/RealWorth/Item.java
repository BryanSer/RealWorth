/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.RealWorth;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 */
public class Item {

    public enum MatchType {
        Same,
        RealSame,
        NotSame;
    }

    private int TypeId;
    private short Durability = 0;
    private String Origin;

    protected Item() {
    }

    public Item(ItemStack s) {
        TypeId = s.getTypeId();
        Durability = s.getDurability();
    }

    public ItemStack toItemStack() {
        return new ItemStack(this.TypeId, 1, this.Durability == -1 ? 0 : this.Durability);
    }

    public Item(String s) {
        Origin = s;
        if (s.matches(".*[:：].*")) {
            String v[] = s.split("[:：]");
            this.TypeId = Integer.parseInt(v[0]);
            if (v[1].equalsIgnoreCase("*")) {
                Durability = -1;
            } else {
                Durability = Short.parseShort(v[1]);
            }
        } else {
            TypeId = Integer.parseInt(s);
        }
    }

    public MatchType match(Item i) {
        if (this.TypeId != i.TypeId) {
            return MatchType.NotSame;
        }
        if (this.Durability != -1) {
            if (this.Durability == i.Durability) {
                return MatchType.RealSame;
            } else {
                return MatchType.NotSame;
            }
        }
        return MatchType.Same;
    }
    
    
    public boolean isThis(Item is) {
        if (is.TypeId != this.TypeId) {
            return false;
        }
        if (this.Durability != -1 && this.Durability != is.Durability) {
            return false;
        }
        return true;
    }

    public boolean isThis(ItemStack is) {
        if (is.getTypeId() != this.TypeId) {
            return false;
        }
        if (this.Durability != -1 && this.Durability != is.getDurability()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.TypeId;
        hash = 89 * hash + this.Durability;
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
        final Item other = (Item) obj;
        if (this.TypeId != other.TypeId) {
            return false;
        }
        if (this.Durability != other.Durability) {
            return false;
        }
        return true;
    }

    public String getOrigin() {
        return Origin;
    }

    @Override
    public String toString() {
        return "Item{" + "TypeId=" + TypeId + ", Durability=" + Durability + ", Origin=" + Origin + '}';
    }

}
