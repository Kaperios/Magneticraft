package com.cout970.magneticraft.gui

import com.cout970.magneticraft.gui.client.*
import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnit
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
 * This class handles which GUI should be opened when a block or item calls player.openGui(...)
 */
object GuiHandler : IGuiHandler {

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z))
        val serverElement = getServerGuiElement(ID, player, world, x, y, z) as ContainerBase
        return when (tile) {
            is TileBox -> GuiBox(serverElement)
            is TileShelvingUnit -> GuiShelvingUnit(serverElement)
            is TileBattery -> GuiBattery(serverElement)
            is TileElectricFurnace -> GuiElectricFurnace(serverElement)
            is TileComputer -> GuiComputer(serverElement)
            is TileCombustionChamber -> GuiCombustionChamber(serverElement)
//            is TileIcebox -> GuiIcebox(serverElement)
//            is TileBrickFurnace -> GuiBrickFurnace(serverElement)
//            is TileGrinder -> GuiGrinder(serverElement)
            else -> null
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val tile = world.getTileEntity(pos)
        return when (tile) {
            is TileBox -> ContainerBox(tile, player, world, pos)
            is TileShelvingUnit -> ContainerShelvingUnit(tile, player, world, pos, ModuleShelvingUnit.Level.values()[ID])
            is TileBattery -> ContainerBattery(tile, player, world, pos)
            is TileElectricFurnace -> ContainerElectricFurnace(tile, player, world, pos)
            is TileComputer -> ContainerComputer(tile, player, world, pos)
            is TileCombustionChamber -> ContainerCombustionChamber(tile, player, world, pos)
//            is TileGrinder -> ContainerGrinder(player, world, BlockPos(x, y, z))
//            is TileBrickFurnace -> ContainerBrickFurnace(player, world, BlockPos(x, y, z))
//            is TileIcebox -> ContainerIcebox(player, world, BlockPos(x, y, z))
            else -> null
        }
    }
}