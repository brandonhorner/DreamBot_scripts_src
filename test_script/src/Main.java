import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.GroundItem;

@ScriptManifest(name = "temporary test script", description = "testing agility course code segments",
        author = "BHORN", version = 1.0, category = Category.AGILITY, image = "")


public class Main extends AbstractScript {

    GroundItem markOfGrace = GroundItems.closest("Mark of Grace");


    public int onLoop() {
        if (markOfGrace != null)
        {
            markOfGrace.interact("Take");
        }
        else
        {
            log("false....");
        }
        return 5000;
    }

}