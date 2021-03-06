package com.khorn.terraincontrol.forge.gui;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.khorn.terraincontrol.TerrainControl;

@SideOnly(Side.CLIENT)
public class TXGuiWorldSelection extends GuiScreen implements GuiYesNoCallback
{
	// Taken from net.minecraft.client.gui.GuiWorldSelection. Changed confirmClicked and disabled rename button
	
    /** The screen to return to when this closes (always Main Menu). */
    protected GuiScreen prevScreen;
    protected String title = "Select world";
    /** Tooltip displayed a world whose version is different from this client's */
    private String worldVersTooltip;
    private GuiButton deleteButton;
    private GuiButton selectButton;
    //private GuiButton renameButton;
    //private GuiButton copyButton;
    private TXGuiListWorldSelection selectionList;
	
    public TXGuiWorldSelection(GuiScreen screenIn)
    {    	
    	this.prevScreen = screenIn;
    }
	
	@Override
    public void confirmClicked(boolean ok, int worldId)
    {
		// Delete existing TC settings
					
		TXGuiListWorldSelectionEntry selectedWorld = this.selectionList.getSelectedWorld();
		
		String worldName = selectedWorld.getSelectedWorldName();
		
		// Delete world
		super.confirmClicked(ok, worldId);

		// Check for world settings for this world
        File TCWorldsDirectory = new File(TerrainControl.getEngine().getTCDataFolder().getAbsolutePath() + "/worlds");
        if(TCWorldsDirectory.exists() && TCWorldsDirectory.isDirectory())
        {
        	for(File worldDir : TCWorldsDirectory.listFiles())
        	{
        		if(worldDir.isDirectory() && worldDir.getName().equals(worldName))
        		{
    				// Only delete temp world files, not WorldBiomes and WorldObjects
    				File StructureDataDirectory = new File(worldDir.getAbsolutePath() + "/StructureData");
                    if (StructureDataDirectory.exists())
                    {
                    	deleteRecursive(StructureDataDirectory);
                    }

                    File structureDataFile = new File(worldDir.getAbsolutePath() + "/StructureData.txt");
                    if (structureDataFile.exists())
                    {
                    	deleteRecursive(structureDataFile);
                    }
                    
                    File nullChunksFile = new File(worldDir.getAbsolutePath() + "/NullChunks.txt");
                    if (nullChunksFile.exists())
                    {
                    	deleteRecursive(nullChunksFile);
                    }
                    
                    File spawnedStructuresFile = new File(worldDir.getAbsolutePath() + "/SpawnedStructures.txt");
                    if (spawnedStructuresFile.exists())
                    {
                    	deleteRecursive(spawnedStructuresFile);
                    }

                    File chunkProviderPopulatedChunksFile = new File(worldDir.getAbsolutePath() + "/ChunkProviderPopulatedChunks.txt");
                    if (chunkProviderPopulatedChunksFile.exists())
                    {
                    	deleteRecursive(chunkProviderPopulatedChunksFile);
                    }

                    File pregeneratedChunksFile = new File(worldDir.getAbsolutePath() + "/PregeneratedChunks.txt");
                    if (pregeneratedChunksFile.exists())
                    {
                    	deleteRecursive(pregeneratedChunksFile);
                    }                    				
    			}
    			break;
    		}
    	}            
		this.mc.displayGuiScreen(new GuiWorldSelection(new GuiMainMenu()));
    }

	public static void deleteRecursive(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	            	deleteRecursive(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
    
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
	@Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            TXGuiListWorldSelectionEntry guilistworldselectionentry = this.selectionList.getSelectedWorld();

            if (button.id == 2)
            {
                if (guilistworldselectionentry != null)
                {
                    guilistworldselectionentry.deleteWorld();
                }
            }
            else if (button.id == 1)
            {
                if (guilistworldselectionentry != null)
                {
                    guilistworldselectionentry.joinWorld();
                }
            }
            else if (button.id == 3)
            {
                this.mc.displayGuiScreen(new GuiCreateWorld(this));
            }
            else if (button.id == 4)
            {
                if (guilistworldselectionentry != null)
                {
                    guilistworldselectionentry.editWorld();
                }
            }
            else if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.prevScreen);
            }
            else if (button.id == 5 && guilistworldselectionentry != null)
            {
                guilistworldselectionentry.recreateWorld();
            }
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.title = I18n.format("selectWorld.title", new Object[0]);
        this.selectionList = new TXGuiListWorldSelection(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
        this.postInit();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.selectionList.handleMouseInput();
    }

    public void postInit()
    {
        this.selectButton = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select", new Object[0])));
        					this.addButton(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create", new Object[0])));
        //this.renameButton = this.addButton(new GuiButton(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.edit", new Object[0])));
        //this.deleteButton = this.addButton(new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete", new Object[0])));
        this.deleteButton = this.addButton(new GuiButton(2, this.width / 2 - 154, this.height - 28, 150, 20, I18n.format("selectWorld.delete", new Object[0])));
        //this.copyButton = this.addButton(new GuiButton(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate", new Object[0])));
        					//this.addButton(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel", new Object[0])));
        					this.addButton(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));
        this.selectButton.enabled = false;
        this.deleteButton.enabled = false;
        //this.renameButton.enabled = false;
        //this.copyButton.enabled = false;
    }
	
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.worldVersTooltip = null;
        this.selectionList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.worldVersTooltip != null)
        {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), mouseX, mouseY);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectionList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.selectionList.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Called back by selectionList when we call its drawScreen method, from ours.
     */
    public void setVersionTooltip(String p_184861_1_)
    {
        this.worldVersTooltip = p_184861_1_;
    }

    public void selectWorld(@Nullable TXGuiListWorldSelectionEntry entry)
    {
        boolean flag = entry != null;
        this.selectButton.enabled = flag;
        this.deleteButton.enabled = flag;
        //this.renameButton.enabled = flag;
        //this.copyButton.enabled = flag;
    }
}