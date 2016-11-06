package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.inventories.get
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.util.COPPER_HEAT_CAPACITY
import com.cout970.magneticraft.util.COPPER_MELTING_POINT
import com.cout970.magneticraft.util.DEFAULT_COOKING_TEMPERATURE
import com.cout970.magneticraft.util.DEFAULT_SMELTING_TEMPERATURE
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */
class TileBrickFurnace : TileHeatBase() {

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = (COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT).toLong(),
            conductivity = 0.05,
            tile = this)

    override val heatNodes: List<IHeatNode>
        get() = listOf(heat)

    val inventory = Inventory()
    var burningTime = 0f

    override fun update() {
        if (!worldObj.isRemote) {
            var smelting_temp = 0.0
            if (inventory[0]?.item is ItemFood) {
                smelting_temp = DEFAULT_COOKING_TEMPERATURE
            } else {
                smelting_temp = DEFAULT_SMELTING_TEMPERATURE
            }
            if (heat.temperature >= smelting_temp && canSmelt()) {
                val applied = heat.temperature / smelting_temp
                heat.pullHeat((applied * FUEL_TO_HEAT).toLong(), false)
                burningTime += (SPEED * applied).toFloat()
                if (burningTime > MAX_BURNING_TIME) {
                    smelt()
                    burningTime -= MAX_BURNING_TIME
                }
            }
            super.update()
        }
    }

    fun canSmelt(): Boolean {
        //has input
        if (inventory[0] == null) return false
        //has recipe
        val result = FurnaceRecipes.instance().getSmeltingResult(inventory[0]) ?: return false
        //is output slot empty
        if (inventory[1] == null) return true
        //or can accept the result
        inventory.ignoreFilter = true
        val ret = inventory.insertItem(1, result, true) == null
        inventory.ignoreFilter = false
        return ret
    }

    fun smelt() {
        inventory.ignoreFilter = true
        val item = inventory.extractItem(0, 1, false)
        val result = FurnaceRecipes.instance().getSmeltingResult(item)?.copy()
        inventory.insertItem(1, result, false)
        inventory.ignoreFilter = false
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setTag("inventory", inventory.serializeNBT())
        setFloat("meltingTime", burningTime)
        super.save()
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        burningTime = nbt.getFloat("meltingTime")
        super.load(nbt)
    }

    companion object {
        val FUEL_TO_HEAT = 0.5f
        val MAX_BURNING_TIME = 100f //100 ticks => 5 seconds
        val SPEED = 1.5 // 50% faster than a vanilla furnace at minimum temperature
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return inventory as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            for (i in 0 until inventory.slots) {
                val item = inventory[i]
                if (item != null) {
                    dropItem(item, pos)
                }
            }
        }
    }

    inner class Inventory : ItemStackHandler(2) {

        var ignoreFilter = false

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            if (slot == 0 || ignoreFilter) {
                return super.insertItem(slot, stack, simulate)
            }
            return stack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            if (slot == 1 || ignoreFilter) {
                return super.extractItem(slot, amount, simulate)
            }
            return null
        }
    }
}