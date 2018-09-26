/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.RealWorth;

import Br.API.Utils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Data.Plugin = this;
        Config.init();
        Data.init();
    }

    @Override
    public void onDisable() {
        Data.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && !sender.isOp()) {
            args = new String[]{"sell"};
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return false;
        }
        if (args[0].equalsIgnoreCase("sell") && (sender instanceof Player)) {
            Player p = (Player) sender;
            ItemStack is = p.getItemInHand();
            if (is == null || is.getType() == Material.AIR || is.getAmount() == 0) {
                p.sendMessage("§c你的手上毛都没有");
                return true;
            }
            BigDecimal price = Tools.getPrice(is);
            if (price == null) {
                p.sendMessage("§c这个物品无法卖出");
                return true;
            }
            BigDecimal sell = Tools.sell(is);
            p.setItemInHand(null);
            Utils.getEconomy().depositPlayer(p.getName(), sell.doubleValue());
            p.sendMessage(String.format("§6本次售卖共获得: %.2f元", sell));//"§6本次售卖共获得:" + sell.doubleValue() + "元");
            return true;
        }
        if (args[0].equalsIgnoreCase("info") && (sender instanceof Player)) {
            Player p = (Player) sender;
            ItemStack is = p.getItemInHand();
            if (is == null || is.getType() == Material.AIR || is.getAmount() == 0) {
                p.sendMessage("§c你的手上毛都没有");
                return true;
            }
            BigDecimal price = Tools.getPrice(is);
            if (price == null) {
                p.sendMessage("§c这个物品无法卖出");
                return true;
            }
            p.sendMessage(String.format("§6这个物品单个售价为: %.2f", price));
            if (is.getAmount() != 1) {
                BigDecimal rate = Tools.getSellRate(is);
                BigDecimal up = BigDecimal.ONE.add(BigDecimal.ONE.add(rate.negate(Data.MathC), Data.MathC).pow(is.getAmount(), Data.MathC).negate(), Data.MathC);
                BigDecimal result = price.multiply(up.divide(rate, Data.MathC), Data.MathC);
                p.sendMessage(String.format("§6手上%d个物品的总价为: %.2f", is.getAmount(), result));
            }
            return true;
        }
        if (sender.isOp()) {
            if (args[0].equalsIgnoreCase("total")) {
                sender.sendMessage(String.format("§e§l当前收购池为: %.2f", Data.getTotalPrice().doubleValue()));
                return true;
            }
            if (args[0].equalsIgnoreCase("settotal")) {
                if (args.length < 2) {
                    return false;
                }
                Map<Item, BigDecimal> data = new HashMap<>();
                for (Item item : Config.getItems()) {
                    BigDecimal totalPrice = Data.getTotalPrice();
                    BigDecimal weight = Tools.getPrice(item).divide(totalPrice, Data.MathC);
                    data.put(item, weight);
                }
                BigDecimal set = new BigDecimal(args[1]);
                BigDecimal totalPrice = Data.getTotalPrice();
                BigDecimal modify = totalPrice.negate().add(set, Data.MathC);
                for (Map.Entry<Item, BigDecimal> e : data.entrySet()) {
                    Data.ModifyDeltaPrice(e.getKey(), e.getValue().multiply(modify));
                }
                sender.sendMessage("§6处理完成");
                return true;
            }
            if (args[0].equalsIgnoreCase("set") && (sender instanceof Player)) {
                if (args.length < 2) {
                    return false;
                }
                Player p = (Player) sender;
                ItemStack is = p.getItemInHand();
                if (is == null || is.getType() == Material.AIR || is.getAmount() == 0) {
                    p.sendMessage("§c你的手上毛都没有");
                    return true;
                }
                Item i = Tools.match(is);
                if (i == null) {
                    p.sendMessage("§c无法设置");
                    return true;
                }
                BigDecimal price = Tools.getPrice(i);
                BigDecimal set = new BigDecimal(args[1]);
                BigDecimal modify = price.add(set.negate(), Data.MathC);
                Data.ModifyDeltaPrice(i, modify);
                p.sendMessage("§6处理完成");
                return true;
            }
            return false;
        }
        return true;
    }

}
