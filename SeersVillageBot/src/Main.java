import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.WallObject;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.Random;

@ScriptManifest(name = "Seer's Village Agility Course", description = "Completes agility course and picks up" +
        " marks of grace!", author = "Brandon Horner", version = 1.01, category = Category.AGILITY, image = "")

public class Main extends AbstractScript
{
    int numberOfLapsCompleted = 0;
    Area start = new Area(2728, 3489, 2730, 3484);
    Tile startTile = new Tile(2729, 3488, 0);

    Area firstRoofArea = new Area(2721, 3496, 2729, 3491, 3); // roof of gap one
    Area secondRoofArea = new Area(2705, 3495, 2713, 3488, 2); // roof of tightrope
    Area thirdRoofArea = new Area(2710, 3481, 2715, 3477, 2); //roof of gap two
    Area fourthRoofArea = new Area(2700, 3475, 2715, 3470, 3); // roof of gap three
    Area fifthRoofArea = new Area(2698, 3465, 2702, 3460, 2); // roof of gap four

    Tile endWallTile = new Tile(2729, 3491, 3);
    Tile endFirstGapTile = new Tile(2713, 3494, 2);
    Tile endTightRopeTile = new Tile(2710, 3480,2);
    Tile endSecondGapTile = new Tile(2710, 3472, 3);
    Tile endThirdGapTile = new Tile(2702, 3465, 2);
    Tile endJumpEdgeTile = new Tile(2704, 3464, 0);

    Tile wallTile = new Tile(2729, 3489, 0);
    Tile firstGapTile = new Tile(2720, 3492, 3);
    Tile tightRopeTile = new Tile(2710, 3489, 2);
    Tile secondGapTile = new Tile(2710, 3476, 2);
    Tile thirdGapTile = new Tile(2700, 3469, 3);
    Tile edgeTile = new Tile(2703, 3461, 2);

    GroundItem markOfGrace;

    WallObject wall;
    GameObject firstGap;
    GameObject tightRope;
    GameObject secondGap;
    GameObject thirdGap;
    GameObject jumpEdge;

    State state;
    InteractionStyle interactionStyle;

    private State getState()
    {
        // first try to assign a mark of grace
        markOfGrace = GroundItems.closest("Mark of Grace");
        // if the variable is null, there isn't a mark around, if there is one around, make sure it is reachable
        if (markOfGrace != null && Map.canReach(markOfGrace.getTile()))
        {
            state = State.TAKING_MARK;
        }
        else if (start.contains(getLocalPlayer().getTile()))
        {
            state = State.CLIMBING_WALL;
        }
        else if (firstRoofArea.contains(getLocalPlayer().getTile()))
        {
            state = State.FIRST_GAP;
        }
        else if (secondRoofArea.contains(getLocalPlayer().getTile()))
        {
            state = State.CROSSING_TIGHTROPE;
        }
        else if (thirdRoofArea.contains(getLocalPlayer().getTile()))
        {
            state = State.SECOND_GAP;
        }
        else if (fourthRoofArea.contains(getLocalPlayer().getTile()))
        {
            state = State.THIRD_GAP;
        }
        else if (fifthRoofArea.contains(getLocalPlayer().getTile()))
        {
            state = State.JUMPING_EDGE;
        }
        else    // otherwise, try to head back to the start
        {
            state = State.BACK_TO_START;
        }
        return state;
    }

    public int onLoop()
    {
        // depending on what state the bot is in, we will complete a different obstacle or pick up a mark.
        switch(getState()){
            case TAKING_MARK:
                markOfGrace.interact("Take");
                break;

            case CLIMBING_WALL:
                log("CLIMBING WALL");  // send "CLIMBING_WALL" to console
                // assign the actual game object "Wall" to our declared variable 'wall'. Check for non-null result,
                //  that the objects' content is "Wall" and check that result contains the wallTile data.
                wall = (WallObject) GameObjects.closest(result -> result != null && result.getName().contentEquals("Wall")
                        && result.getTile().equals(wallTile));

                if(!start.contains(getLocalPlayer()) && wall.isOnScreen()) {
                    if(!getLocalPlayer().isMoving())
                    {
                        Walking.walk(startTile);
                        sleepUntil(() -> start.contains(getLocalPlayer()), 5000);
                    }
                }

                // randomize between the possible interaction styles
                randomInteraction(wall, "Climb-up");

                sleepUntil(() -> getLocalPlayer().getTile().equals(endWallTile), 4000);
                break;

            case FIRST_GAP:
                log("FIRST GAP");
                firstGap = GameObjects.closest(result -> result != null && result.getName().contentEquals("Gap")
                        && result.getTile().equals(firstGapTile));

                firstGap.interact();

                sleepUntil(() -> getLocalPlayer().getTile().equals(endFirstGapTile), 8000);
                break;

            case CROSSING_TIGHTROPE:
                log("CROSSING TIGHTROPE");
                tightRope = GameObjects.closest(result-> result != null && result.getName().contentEquals("Tightrope")
                        && result.getTile().equals(tightRopeTile));

                randomInteraction(tightRope, "Cross");

                sleepUntil(() -> getLocalPlayer().getTile().equals(endTightRopeTile), 8000);
                break;

            case SECOND_GAP:
                log("SECOND GAP");
                secondGap = GameObjects.closest(result -> result != null && result.getName().contentEquals("Gap")
                        && result.getTile().equals(secondGapTile));

                randomInteraction(secondGap, "Jump");

                sleepUntil(() -> getLocalPlayer().getTile().equals(endSecondGapTile), 8000);
                break;

            case THIRD_GAP:
                log("THIRD GAP");
                thirdGap = GameObjects.closest(result -> result != null && result.getName().contentEquals("Gap")
                        && result.getTile().equals(thirdGapTile));

                randomInteraction(thirdGap, "Jump");

                sleepUntil(() -> getLocalPlayer().getTile().equals(endThirdGapTile), 3000);
                break;

            case JUMPING_EDGE:
                log("JUMPING OFF EDGE");
                jumpEdge = GameObjects.closest(result -> result != null && result.getName().contentEquals("Edge")
                        && result.getTile().equals(edgeTile));

                randomInteraction(jumpEdge, "Jump");

                sleepUntil(() -> getLocalPlayer().getTile().equals(endJumpEdgeTile), 8000);
                numberOfLapsCompleted++;
                log("Amount of laps finished (roughly) = " + numberOfLapsCompleted);
                break;

            case BACK_TO_START:
                log("GOING BACK TO START");
                // if we aren't at the start, go back to the start tile
                if (Walking.shouldWalk())
                {
                    Walking.walk(startTile);
                }
                sleep(3000, 5000);
                break;
        }
        // loop every n milliseconds
        return 1000;
    }

    // state names
    private enum State
    {
        TAKING_MARK, CLIMBING_WALL, FIRST_GAP, CROSSING_TIGHTROPE,
        SECOND_GAP, THIRD_GAP, JUMPING_EDGE, BACK_TO_START
    }

    // possible interaction types
    private enum InteractionStyle
    {
        MOUSECLICK, BASIC_INTERACTION, INTERACTION
    }

    private InteractionStyle getInteractionStyle()
    {
        // construct a new object to choose a random number
        Random randNum = new Random();
        // 3 possible decisions 0, 1, 2 (upperbound is excluded)
        int upperbound = 3;
        // generate a random integer from the randNum object
        int randomDecision = randNum.nextInt(upperbound);

        if (randomDecision == 0)
            interactionStyle = InteractionStyle.MOUSECLICK;

        else if (randomDecision == 1)
            interactionStyle = InteractionStyle.BASIC_INTERACTION;

        else
            interactionStyle = InteractionStyle.INTERACTION;

        return interactionStyle;
    }

    private void randomInteraction(GameObject gameObject, String action)
    {

        switch(getInteractionStyle())
        {
            // left click the object
            default:
                Mouse.click(gameObject.getClickablePoint());
                break;

            // left click the wall to interact????
            case BASIC_INTERACTION:
                gameObject.interact();
                break;

            // right click the wall and select climb-up
            case INTERACTION:
                gameObject.interact(action);
                break;
        }
        return;
    }

}