package com.spaceman.tport.events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.spaceman.tport.Main;

public class InventoryClick implements Listener {

	private Main p;

	public InventoryClick(Main instance) {
		p = instance;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();

		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}

		ItemMeta meta = item.getItemMeta();

		if (meta == null) {
			return;
		}
		if (meta.getDisplayName() == null) {
			return;
		}
		if (inv.getTitle().contains("choose a player")) {

			if (meta.getDisplayName().equals(ChatColor.DARK_AQUA + "next")) {

				int b = p.getConfig().getInt("tport." + player.getName() + ".gui");
				int c = 0;
				b = b + 7;
				int i = 10;

				p.getConfig().set("tport." + player.getName() + ".gui", b);
				p.saveConfig();

				Inventory inve = Bukkit.createInventory(null, 45, "choose a player (" + (b + 8) / 7 + ")");

				for (String ss : p.getConfig().getConfigurationSection("tport").getKeys(false)) {

					if (!(c <= b)) {

						p.getConfig().set("tport." + player.getName() + ".gui", b);

						if (i >= 35) {
							ItemStack items = new ItemStack(Material.HOPPER);
							ItemMeta metas = items.getItemMeta();
							metas.setDisplayName(ChatColor.DARK_AQUA + "next");
							items.setItemMeta(metas);
							inve.setItem(44, items);

							p.getConfig().set("tport." + player.getName() + ".gui", b);
							p.saveConfig();
							break;
						}

						if (i == 17 || i == 26) {
							i = i + 2;
						}
						if (!(i == 44)) {
							inve.setItem(i, p.getConfig().getItemStack("tport." + ss + ".item"));
							i++;

							ItemStack itemsa = new ItemStack(Material.LONG_GRASS, 1, (byte) 2);
							ItemMeta metasa = itemsa.getItemMeta();
							metasa.setDisplayName(ChatColor.DARK_AQUA + "previous");
							itemsa.setItemMeta(metasa);
							inve.setItem(8, itemsa);

						}
					} else {
						c++;
					}
				}
				player.openInventory(inve);

			}

			if (meta.getDisplayName().equals(ChatColor.DARK_AQUA + "previous")) {
				int b = p.getConfig().getInt("tport." + player.getName() + ".gui");
				int c = 0;
				b = b - 7;
				int i = 10;
				Inventory inve = Bukkit.createInventory(null, 45, "choose a player (" + (b + 8) / 7 + ")");

				for (String ss : p.getConfig().getConfigurationSection("tport").getKeys(false)) {

					if (!(c <= b)) {

						if (!(b == -1)) {
							ItemStack itemsa = new ItemStack(Material.LONG_GRASS, 1, (byte) 2);
							ItemMeta metasa = itemsa.getItemMeta();
							metasa.setDisplayName(ChatColor.DARK_AQUA + "previous");
							itemsa.setItemMeta(metasa);
							inve.setItem(8, itemsa);
						}

						if (i >= 35) {
							ItemStack items = new ItemStack(Material.HOPPER);
							ItemMeta metas = items.getItemMeta();
							metas.setDisplayName(ChatColor.DARK_AQUA + "next");
							items.setItemMeta(metas);
							inve.setItem(44, items);

							p.getConfig().set("tport." + player.getName() + ".gui", b);
							p.saveConfig();
							break;
						}

						if (i == 17 || i == 26) {
							i = i + 2;
						}
						if (!(i == 44)) {
							inve.setItem(i, p.getConfig().getItemStack("tport." + ss + ".item"));
							i++;

						}
					} else {
						c++;
					}
				}
				player.openInventory(inve);

			}

			Inventory invc = Bukkit.createInventory(null, 9,"tport: " + meta.getDisplayName());

			for (String s : p.getConfig().getConfigurationSection("tport").getKeys(false)) {

				if (meta.getDisplayName().equals(s)) {

					for (int i = 0; i < 7; i++) {

						if (s.equals(player.getName())) {
							invc.setItem(i, p.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));
						} else if (p.getConfig().contains("tport." + s + ".items." + i)) {

							if (p.getConfig().getString("tport." + s + ".items." + i + ".private.statement")
									.equals("false")) {

								invc.setItem(i, p.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));

							} else if (p.getConfig().getString("tport." + s + ".items." + i + ".private.statement")
									.equals("true")) {

								ArrayList<String> list = (ArrayList<String>) p.getConfig()
										.getStringList("tport." + s + ".items." + i + ".private.players");
								if (list.contains(player.getName())) {
									invc.setItem(i, p.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));
								}
							}
						}
						ItemStack back = new ItemStack(Material.BARRIER);
						ItemMeta metaback = back.getItemMeta();
						metaback.setDisplayName("back");
						back.setItemMeta(metaback);
						invc.setItem(8, back);

						ItemStack warp = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
						SkullMeta skin = (SkullMeta) warp.getItemMeta();
						skin.setOwner(s);

						if (p.getConfig().getString("tport." + s + ".tp.statement").equals("off")) {

							ArrayList<String> list = (ArrayList<String>) p.getConfig()
									.getStringList("tport." + s + ".tp.players");

							if (!list.contains(player.getName())) {
								skin.setDisplayName("player tp is off");
							} else {
								skin.setDisplayName("warp to " + s);
							}
						} else if (Bukkit.getPlayerExact(s) != null) {
							skin.setDisplayName("warp to " + s);
						} else {
							skin.setDisplayName("player not online");
						}
						warp.setItemMeta(skin);
						invc.setItem(7, warp);

					}
					e.setCancelled(true);
					player.openInventory(invc);

				} else {
					e.setCancelled(true);
				}
			}

		} else {

			for (String s : p.getConfig().getConfigurationSection("tport").getKeys(false)) {

				if (inv.getTitle().equals("tport: " + s)) {

					if (item.getType().equals(Material.BARRIER)) {
						if (meta.getDisplayName().equals("back")) {
							if (e.getSlot() == 8) {

								int b = p.getConfig().getInt("tport." + player.getName() + ".gui");
								int c = 0;
								int i = 10;
								Inventory inve = Bukkit.createInventory(null, 45,
										"choose a player (" + (b + 8) / 7 + ")");

								for (String ss : p.getConfig().getConfigurationSection("tport").getKeys(false)) {

									if (!(c <= b)) {

										if (i >= 35) {
											ItemStack items = new ItemStack(Material.HOPPER);
											ItemMeta metas = items.getItemMeta();
											metas.setDisplayName(ChatColor.DARK_AQUA + "next");
											items.setItemMeta(metas);
											inve.setItem(44, items);

											p.getConfig().set("tport." + player.getName() + ".gui", b);
											p.saveConfig();
											break;
										}

										if (i == 17 || i == 26) {
											i = i + 2;
										}
										if (!(i == 44)) {
											inve.setItem(i, p.getConfig().getItemStack("tport." + ss + ".item"));
											i++;

											if (!p.getConfig().get("tport." + player.getName() + ".gui").equals(-1)) {

												ItemStack itemsa = new ItemStack(Material.LONG_GRASS, 1, (byte) 2);
												ItemMeta metasa = itemsa.getItemMeta();
												metasa.setDisplayName(ChatColor.DARK_AQUA + "previous");
												itemsa.setItemMeta(metasa);
												inve.setItem(8, itemsa);
											}
										}
									} else {
										c++;
									}
								}
								player.openInventory(inve);

							} else {
								e.setCancelled(true);
							}
						}
					}

					if (meta.getDisplayName().equals("player tp is off")) {
						e.setCancelled(true);
					}

					if (item.getType().equals(Material.SKULL_ITEM)) {
						if (meta.getDisplayName().equals("player not online")) {

							if (Bukkit.getPlayerExact(inv.getTitle().replaceAll("tport:", "").trim()) != null) {
								Player warp = Bukkit.getPlayerExact(inv.getTitle().replaceAll("tport:", "").trim());
								player.sendMessage("§3teleported to §9" + warp.getName());
								player.teleport(warp.getLocation());
							}

							e.setCancelled(true);
						} else if (meta.getDisplayName().equals("warp to " + s)) {
							if (Bukkit.getPlayerExact(s) == null) {

								Inventory invc = Bukkit.createInventory(null, 9,"tport: " + s);
								for (int i = 0; i < 7; i++) {

									if (s.equals(player.getName())) {
										invc.setItem(i,
												p.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));
									} else

									if (p.getConfig().contains("tport." + s + ".items." + i)) {

										if (p.getConfig().getString("tport." + s + ".items." + i + ".private.statement")
												.equals("false")) {

											invc.setItem(i,
													p.getConfig().getItemStack("tport." + s + ".items." + i + ".item"));

										} else if (p.getConfig()
												.getString("tport." + s + ".items." + i + ".private.statement")
												.equals("true")) {

											ArrayList<String> list = (ArrayList<String>) p.getConfig()
													.getStringList("tport." + s + ".items." + i + ".private.players");
											if (list.contains(player.getName())) {
												invc.setItem(i, p.getConfig()
														.getItemStack("tport." + s + ".items." + i + ".item"));
											}
										}
									}
									ItemStack back = new ItemStack(Material.BARRIER);
									ItemMeta metaback = back.getItemMeta();
									metaback.setDisplayName("back");
									back.setItemMeta(metaback);
									invc.setItem(8, back);

									ItemStack warp = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
									SkullMeta skin = (SkullMeta) warp.getItemMeta();
									skin.setOwner(s);

									if (Bukkit.getPlayerExact(s) != null) {
										skin.setDisplayName("warp to " + s);
									} else {
										skin.setDisplayName("player not online");
									}
									warp.setItemMeta(skin);
									invc.setItem(7, warp);

								}
								e.setCancelled(true);
								player.openInventory(invc);

							} else {
								Player warp = Bukkit.getPlayerExact(s);
								e.setCancelled(true);
								player.sendMessage("§3teleported to §9" + warp.getName());
								player.teleport(warp.getLocation());
							}
						}
					}
					for (int i = 0; i < 7; i++) {
						if (p.getConfig().contains("tport." + s + ".items." + i + ".item")) {
							ItemStack items = p.getConfig().getItemStack("tport." + s + ".items." + i + ".item");
							if (meta.getDisplayName().equals(items.getItemMeta().getDisplayName())) {
								if (e.getSlot() == 0 || e.getSlot() == 1 || e.getSlot() == 2 || e.getSlot() == 3
										|| e.getSlot() == 4 || e.getSlot() == 5 || e.getSlot() == 6) {
									e.setCancelled(true);
									player.closeInventory();
									player.teleport(
											(Location) p.getConfig().get("tport." + s + ".items." + i + ".location"));
									player.sendMessage("§3teleported to §9" + items.getItemMeta().getDisplayName());

								} else {
									e.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}
}