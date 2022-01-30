import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ExploreForest8
{
    // Forest record
    public static class Forest {
        int kind; // 1 = animal, 2 = plant
        String name;
        int healingPointGain;
        boolean isDangerous;
    }

    // Healer Record
    public static class Healer {
        String name;
        int healingPoint;
        int survivalScore;
    }

    public static void main(String[] args) throws IOException
    {
        String name;
        Forest[] organismArray = forestArray();
        ArrayList<Forest> encounteredAnimals = new ArrayList<>();

        File gameFile = new File("healer.csv");
        if (gameFile.exists())
        {
            String response = inputYesNo("do you want to continue from last save? (yes/no) ");
            if (response.toLowerCase().equals("yes"))
            {
                String[] gameInfo = readHealer();
                name = gameInfo[0];
                int healingPoints = Integer.parseInt(gameInfo[1]);
                int survivalScore = Integer.parseInt(gameInfo[2]);
                int roundNo = Integer.parseInt(gameInfo[3]);
                Healer player = createHealer(name, healingPoints, survivalScore);

                heal(roundNo, player, organismArray, encounteredAnimals);

                print("\nyou have no more survival points left");
                print("your final score is " + getHealingPoint(player));
                displayEncountered(encounteredAnimals);

            } else {
                name = askName();
                Healer player = createHealer(name, 10, 4);

                heal(1, player, organismArray, encounteredAnimals);

                print("\nyou have no more survival points left");
                print("your final score is " + getHealingPoint(player));
                displayEncountered(encounteredAnimals);
            }
        }
    }
    //**************************************************************************
    // ADT for Forest
    // Operations:
    // -create a Forest record by providing the name, healing points gained
    //      and if it is dangerous or not
    // -create an array of Forest organisms that will be used throughout the program
    // -get a random Forest record from the Forest array
    // -get each field of the Forest record
    //**************************************************************************

    // initialises the array of type Forest of organisms to be encountered by the user
    public static Forest[] forestArray()
    {
        Forest koala = createForest(1, "koala", 5, false);
        Forest bear = createForest(1, "bear", 9, true);
        Forest lynx = createForest(1, "lynx", 7, true);
        Forest coyote = createForest(1, "coyote", 6, true);
        Forest puma = createForest(1, "puma", 8, true);
        Forest fox = createForest(1, "fox", 5, false);
        Forest rabbit = createForest(1, "rabbit", 4, false);
        Forest wolf = createForest(1, "wolf", 6, true);
        Forest foxGlove = createForest(2, "foxglove", -3, true);
        Forest monkshood = createForest(2, "monkshood", -4, true);
        Forest poisonHemlock = createForest(2, "poison hemlock", -8, true);
        Forest sunflower = createForest(2, "sunflower", 3, false);
        Forest bellflower = createForest(2, "bellflower", 2, false);

        Forest[] forestArr = {koala, bear, lynx, coyote, puma, rabbit, fox, wolf, foxGlove, monkshood, poisonHemlock, sunflower, bellflower};
        return forestArr;
    }

    // creates a new forest record instance
    public static Forest createForest(int kind, String name, int healingPointGain, boolean isDangerous)
    {
        Forest f = new Forest();
        f.kind = kind;
        f.name = name;
        f.healingPointGain = healingPointGain;
        f.isDangerous = isDangerous;

        return f;
    }

    // Getters
    public static String getForestName(Forest f) {
        return f.name;
    }

    public static int getForestKind(Forest f) {
        return f.kind;
    }

    public static int getForestPoints(Forest f) {
        return f.healingPointGain;
    }

    public static boolean getForestThreat(Forest f) {
        return f.isDangerous;
    }

    // this method gets a random organism from the Forest[] array
    // and returns it
    public static Forest randomOrganism(Forest[] forestArr)
    {
        int randomPos = randomInt(forestArr.length);
        Forest organism = forestArr[randomPos];

        return organism;
    }

    //*********************************************************************************************
    // Healer accessor methods
    // Setters
    public static Healer setHealingPoint(Healer h, int amount) {
        h.healingPoint += amount;
        return h;
    }

    public static Healer setSurvivalScore(Healer h, int amount) {
        h.survivalScore += amount;
        return h;
    }

    // Getters
    public static String getName(Healer h) {
        return h.name;
    }

    public static int getHealingPoint(Healer h) {
        return h.healingPoint;
    }

    public static int getSurvivalScore(Healer h) {
        return h.survivalScore;
    }

    //***********************************************************************************************

    // creates a new instance of the Healer record
    public static Healer createHealer(String name, int healingPoints, int survivalScore)
    {
        Healer h = new Healer();
        h.name = name;
        h.healingPoint = healingPoints;
        h.survivalScore = survivalScore;
        return h;
    }

    // outputs the situation the user is facing, also stating if the organism is dangerous or not
    public static void healingObjective(String name, Forest organism)
    {
        print(name + ", you are in a forest and you can see a " + getForestName(organism) + " that needs to be healed...");

        if (getForestThreat(organism))
        {
            print("beware, it is dangerous");
        } else {
            print("the " + getForestName(organism) + " is safe");
        }

    }

    // asks for the users Name and returns it
    public static String askName()
    {
        String userName = inputString("What is your adventurer name? ");
        return userName;
    }

    // generates a random number
    public static int randomInt(int bound)
    {
        Random r = new Random();
        return r.nextInt(bound );
    }

    // rolls a dice giving a random number from 1 to 6
    public static int diceRoll()
    {
        int dice = randomInt(6) + 1;
        return dice;
    }

    // updates the survivalScore of the player if dice is less than their survival score
    // they have defended against an animal attack
    public static Healer defend(Healer healer)
    {
        int dice = diceRoll();

        if (dice < getSurvivalScore(healer))
        {
            print("you have defended against the attack");
        } else {
            print("the animal has attacked you, -1 survival point!");
            setSurvivalScore(healer, -1);
        }
        return healer;
    }


    // healing method specific to plants, since some plants are poisonous
    public static Healer healPlant(Healer healer, int injuryScore, Forest organism)
    {
        String answer = inputYesNo("Do You wish to heal the plant? (yes/no) ");

        final int HP_LOSS = -2;

        int dice1;
        int dice2;
        int total;

        if (answer.toLowerCase().equals("yes")) {
            dice1 = diceRoll();
            dice2 = diceRoll();
            total = dice1 + dice2;

            print("you have rolled two die, the total is " + total);

            if (total > injuryScore) {
                int points = getForestPoints(organism);

                if (getForestThreat(organism)) {
                    print("The plant is poisonous and you have been poisoned, " + points + " healing point and -2 from your survival score!");
                    setSurvivalScore(healer, HP_LOSS);

                } else {
                    print("The plant was healed successfully, +" + points + " healing point!");
                }
                setHealingPoint(healer, points);
            } else {
                print("Healing the plant was unsuccessful, -2 healing points!");
                print(" \n...");
                setHealingPoint(healer, HP_LOSS);
            }
        }
        else
        {
            print("You did not heal the plant");
            setHealingPoint(healer, 0);
        }
        return healer;
    }

    // prints out that the animal has been healed otherwise the animal attacks
    // and returns Healer record
    public static Healer heal(int round, Healer healer, Forest[] animalArr, ArrayList<Forest> encountered) throws IOException
    {
        int injuryScore;
        final int HP_LOSS = -2;

        int dice1;
        int dice2;
        int total = 0;

        //int counter = 1;
        while (getSurvivalScore(healer) > 0)
        {
            Forest organism = randomOrganism(animalArr); // gets a random forest record from array
            encountered.add(organism);

            print("\nRound " + round + ":"); // prints the current round number
            injuryScore = randomInt(10) + 1;
            healingObjective(getName(healer), organism);
            print("it has an injury level of " + injuryScore);

            if (getForestKind(organism) == 1) { // if organism is an animal
                String answer = inputYesNo("Do You wish to heal the animal? (yes/no) ");

                if (answer.toLowerCase().equals("yes")) {
                    dice1 = diceRoll();
                    dice2 = diceRoll();
                    total = dice1 + dice2;

                    print("you have rolled two die, the total is " + total);

                    if (total > injuryScore) {
                        int points = getForestPoints(organism);
                        print("The animal was healed successfully, +" + points + " healing point!");
                        setHealingPoint(healer, points);
                    } else {
                        print("Healing was unsuccessful, -2 healing points!");
                        setHealingPoint(healer, HP_LOSS);
                        if (getForestThreat(organism)) {
                            print("The animal is angry and is attacking \n...");
                            defend(healer);  // to defend against the attack
                            answer = inputYesNo("Do you want to try and heal it again? (yes/no)");

                            while (answer.equals("yes")) {
                                total = diceRoll() + diceRoll();
                                print("you have rolled two die again, the total is " + total);

                                if (total > injuryScore) {
                                    print("The animal was healed successfully, +1 healing point!");
                                    setHealingPoint(healer, 1);
                                    answer = "no";
                                } else {
                                    print("Healing was unsuccessful, -2 healing points!");
                                    setHealingPoint(healer, HP_LOSS);
                                    print("The animal is angry and is attacking \n...");
                                    defend(healer);
                                    answer = inputYesNo("Do you want to try and heal it again? (yes/no)");
                                }
                            }
                        } else {
                            print("the animal is not a threat, you walk away unscathed. ");
                        }
                    }
                } else {
                    print("You did not heal the animal");
                    setHealingPoint(healer, 0);
                }
            } else { // or else the organism is a plant
                healer = healPlant(healer, injuryScore, organism);
            }

            displayEncountered(encountered);
            printScores(healer); // printing player scores

            String askSave = inputYesNo("do you want to save and quit game? (yes/no)");
            if (askSave.toLowerCase().equals("yes"))
            {
                saveHealer(healer, round);
                System.exit(0);
            }
            round++;
        }
        return healer;
    }

    // saves the current progress of the game: all the data within Healer
    public static void saveHealer(Healer healer, int roundNumber) throws IOException
    {
        PrintWriter file = new PrintWriter(new FileWriter("healer.csv"));
        file.println(getName(healer) + "," + getHealingPoint(healer) + "," + getSurvivalScore(healer) + "," + roundNumber);
        file.close();
    }

    // reads the data stored in the csv file of the healer and saves it to an array
    public static String[] readHealer() throws IOException
    {
        BufferedReader file = new BufferedReader(new FileReader("healer.csv"));

        String gameInfoRead = file.readLine();
        String[] gameInfo = gameInfoRead.split(",");

        file.close();
        return gameInfo;
    }

    // output method which prints out the current healing and survival points of the player
    public static void printScores(Healer healer)
    {
        if(getHealingPoint(healer) < 0) {
            print("\n|> Healing Points: 0");
        } else {
            print( "\n|> Healing Points: " + getHealingPoint(healer));
        }

        if(getSurvivalScore(healer) < 0) {
            print("|> Survival Score: 0");
        } else {
            print("|> Survival Score: " + getSurvivalScore(healer));
        }
    }

    // displays all the animals encountered so far by the player
    public static void displayEncountered(ArrayList<Forest> encountered)
    {
        String viewEncountered = inputYesNo("\ndo you want to view all the organisms encountered so far? (yes/no)");
        if (viewEncountered.toLowerCase().equals("yes"))
        {
            bubblesortList(encountered);
            print("all animals in order of healing points gained is: ");
            for(int i = 0; i < encountered.size(); i++)
            {
                print("#"+(i+1)+" "+getForestName(encountered.get(i))+", healing points gained: " + getForestPoints(encountered.get(i)));
            }
        }
    }

    // bubble sort which sorts the list of encountered animals in order of healing points gained
    public static void bubblesortList(ArrayList<Forest> forestList)
    {
        int length = forestList.size();
        int pass = 0;
        boolean isSwapped = true;
        Forest temp;
        while ((pass <= length - 2) && (isSwapped))
        {
            isSwapped = false;
            for (int position = 0; position <= length-2-pass; position++)
            {
                if (getForestPoints(forestList.get(position)) < getForestPoints(forestList.get(position + 1)))
                {
                    temp = forestList.get(position + 1);
                    forestList.set(position + 1, forestList.get(position));
                    forestList.set(position, temp);
                    isSwapped = true;
                }
            }
        }
    }

    // general method that returns the response given by the user after being prompted
    public static String inputString(String message)
    {
        Scanner scanner = new Scanner(System.in);
        print(message);
        String response = scanner.nextLine();
        return  response;
    }

    // input validation method, only takes inputs of yes or no
    public static String inputYesNo(String message)
    {
        String response = inputString(message);

        while(!(response.toLowerCase().equals("yes") || response.toLowerCase().equals("no")))
        {
            response = inputString("please enter either yes or no");
        }
        return response;
    }

    //prints out a string that passed to it
    public static void print(String message) {
        System.out.println(message);
    }
}
