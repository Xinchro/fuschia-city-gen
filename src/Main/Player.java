package Main;

import com.jme3.scene.Spatial;

/**
 * 
 * This class was taken from another project (For CI328)
 *
 * @author Xinchro
 */
public class Player {
    
    private int health = 10;
    private int maxHealth = health;
    private String name;
    private int pwr = 5;
    private int dex = 3;
    private int wis = 4;
    private boolean dead;
    private Spatial model;
    private int level = 1;
    private int experience = 0;
    private int levelCap = 2;
    private int statPoints = 10;
    
    public Player(){
	name = "defaultPlayerName";
	health = 50;
	maxHealth = health;
	printStats();
    }
    
    /*
     * Method to get the model/image
     * 
     * @return model
     */
    public Spatial getModel(){
        //model = new createjs.Bitmap("Assets/Models/Burrito1.svg");
        return model;
    };
    
    /*
     * Method to force the health to a certain value
     */
    public void setHealth(int inHealth){
        health = inHealth;
    };
    
    /*
     * Method to get the current health
     * 
     * @return health
     */
    public int getHealth(){
        return health;
    };
    
    /*
     * Method to force the maximum health to a certain value
     */
    public void setMaxHealth(int inHealth){
        maxHealth = inHealth;
    };
    
    /*
     * Method to get the maximum health
     * 
     * @return maxHealth
     */
    public int getMaxHealth(){
        return maxHealth;
    };
    
    /*
     * Method to force the name into a different one
     */
    public void setName(String inName){
        name = inName;
    };
    
    /*
     * Method to get the name
     * 
     * @return name
     */
    public String getName(){
        return name;
    };
    
    /*
     * Method to check if the enemy is dead
     * 
     * @return dead
     */
    public boolean isDead(){
        return dead;
    };
    
    /*
     * Method to attack a target with the current power
     */
    public void attack(Object target){
        //decrement the target's health by the power
        //Object.decrementHealth(pwr);
    };
    
    /*
     * Method to attack with a special attack
     * Only attacks for double the power for now
     */
    public void specialAttack(Object target){
        //attack the target with double the power
        //target.decrementHealth(pwr*2);
    };
    
    /*
     * Method to decremenet the health by a certain value, or just one if value is ommited
     */
    public void decrementHealth(int decrement){
        //System.out.println("enemy health going from " + health + " because " + decrement);
	//if health is about to drop to 0 or below
	if(health-decrement<=0){
	    //set health to 0
	    health = 0;
	    //set dead
	    dead = true;
	    //increment enemies killed score
	    //enemiesKilled++;
	    //give the player experience
	    giveExp(1);
	    //print the player's stats to System.out
	    printStats();
	}else{
	    //decrement health by the input
	    health = health - decrement;
        }
        //System.out.println("to " + health);
    };
    
    /*
     * Method to increment the health
     */
    public void incrementHealth(int increment){
	//if the health is about to hit, or go above, the maximum health
	if(health + increment >= maxHealth){
	    //current health becomes maximum health
	    health = maxHealth;
	}else{
	    //increment gets added to health
	    health += increment;
	}
    };
    
    //variable to decide on special attack rarity, the higher the value the more rare
    int chanceModifier=5;
    
    /*
     * Method to attack
     */
    public void doAttack(){
        //get a random number, minimum once, maximum the chanceModifier
        int randChance = (int) Math.floor((Math.random()*chanceModifier)+1);
        //if it is equal to once (1 in a X chance)
        if(randChance == 1){
            //special attack on the player
            //this.specialAttack(player);
            //System.out.println("enemy uses special attack " + randChance + " " + chanceModifier);
        }else{
            //normal attack on the player
            //this.attack(player);
            //System.out.println("enemy uses normal attack " + randChance + " " + chanceModifier);
        }
    };
    
    /*
     * Method to the set the enemy to dead
     */
    private void setDead(){
        dead = true;
    };
    
    /*
     * Method to force the exp to a certain value
     */
    public void setExp(int newExp){
        experience = newExp;
        //check for a level up
        checkLevelUp();
    };
        
    /*
     * Method to get the current exp
     * 
     * @return experience
     */
    public int getExp(){
        return experience;
    };
    
    /*
     * Method to get the exp required for the next level up
     * 
     * @return gap
     */
    public int getExpToNextLevel(){
        int gap = levelCap - experience;
        return gap;
    };
    
    /*
     * Method to give an amount of experience
     */
    public void giveExp(int addExp){
        //add the exprience to the current experience
        experience += addExp;
        //check for a level up
        checkLevelUp();
    };
    
    /*
     * Method to check for a level up
     */
    private void checkLevelUp(){
        //console.log("Checking levelCap/exp: " + levelCap + " " + experience);
        //
        //while the experience is above the cap loop through this
        while(experience >= levelCap){
            System.out.println("----LEVEL UP----");
            //increment the  level
            level += 1;
            //remove the current level cap from the experience pool
            experience -= levelCap;
            //ramp up the level cap
            levelCap += Math.ceil(levelCap*0.5);
            //print the player's stats
            printStats();
        }
    };
    
    /*
     * Method to print the player's stats to console
     */
    public void printStats(){
        System.out.println("------Player Stats------");
        System.out.println("Level: " + level);
        System.out.println("Exp: " + experience);
        System.out.println("Level Cap: " + levelCap);
        System.out.println("Max Health: " + maxHealth);
        System.out.println("Health: " + health);
        System.out.println("Power: " + pwr);
        System.out.println("Dex: " + dex);
        System.out.println("Will: " + wis);
        System.out.println("------End Player Stats------");
    };
}
