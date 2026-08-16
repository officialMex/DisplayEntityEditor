package goldenshadow.displayentityeditor;

import goldenshadow.displayentityeditor.enums.LockSearchMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class SelectionMode {

    private static final Predicate<Entity> DISPLAY_FILTER = entity -> entity instanceof Display;
    private static final Function<Entity, Display> DISPLAY_CAST = entity -> (Display) entity;

    private static final HashMap<String, SelectionMode> idToMode = new HashMap<>();
    private static final ArrayList<String> idOrder = new ArrayList<>();

    public static int amount() {
        return idOrder.size();
    }

    public static SelectionMode get(String mode) {
        return idToMode.get(mode);
    }

    public static final SelectionMode NEARBY = new SelectionMode("nearby") {

        @Override
        protected Stream<Display> select(Player p, double range, Predicate<Display> lockFilter) {
            return p.getWorld().getNearbyEntities(p.getLocation(), range, range, range).stream().filter(DISPLAY_FILTER).map(DISPLAY_CAST)
                    .filter(lockFilter);
        }
    };

    public static final SelectionMode RAYCAST = new SelectionMode("raycast") {

        @Override
        protected Stream<Display> select(Player p, double range, Predicate<Display> lockFilter) {
            Location loc = p.getEyeLocation();
            Vector direction = loc.getDirection().normalize();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            double dx = direction.getX();
            double dy = direction.getY();
            double dz = direction.getZ();
            World world = p.getWorld();
            List<Display> displays;
            for (double distance = 0d; distance < range; distance += 0.25d) {
                displays = world.getNearbyEntities(loc = new Location(world, x + dx * distance, y + dy * distance, z + dz * distance),
                        0.75d, 0.25d, 0.75d).stream().filter(DISPLAY_FILTER).map(DISPLAY_CAST).filter(lockFilter).toList();
                if (displays.isEmpty()) {
                    continue;
                }
                double tmp;
                Display closest = null;
                distance = Double.MAX_VALUE;
                for (Display display : displays) {
                    tmp = loc.distanceSquared(display.getLocation());
                    if (tmp < distance) {
                        distance = tmp;
                        closest = display;
                    }
                }
                return Stream.of(closest);
            }
            return Stream.empty();
        }
    };

    private final String id;

    public SelectionMode(String id) {
        this.id = id;
        idOrder.add(id);
        idToMode.put(id, this);
    }

    public final String id() {
        return this.id;
    }

    public final int index() {
        return idOrder.indexOf(this.id);
    }

    public final SelectionMode previousMode() {
        int i = idOrder.indexOf(this.id);
        return idToMode.get(i == 0 ? idOrder.getLast() : idOrder.get(i - 1));
    }

    public final SelectionMode nextMode() {
        int i = idOrder.indexOf(this.id);
        return idToMode.get(i + 1 == idOrder.size() ? idOrder.getFirst() : idOrder.get(i + 1));
    }

    public final List<Display> select(Player p, LockSearchMode lockSearchMode) {
        List<Display> displays = select(p, Utilities.getToolSelectRange(p), lockSearchMode.getPredicate()).toList();
        if (displays.isEmpty()) {
            return null;
        }
        if (displays.size() == 1) {
            return displays;
        }
        if (Utilities.getToolSelectMultiple(p)) {
            return displays;
        }
        Display closest = null;
        double tmp, distance = Double.MAX_VALUE;
        Location pLoc = p.getLocation();
        for (Display display : displays) {
            tmp = pLoc.distanceSquared(display.getLocation());
            if (tmp < distance) {
                distance = tmp;
                closest = display;
            }
        }
        // Never null
        return List.of(closest);
    }

    protected abstract Stream<Display> select(Player p, double range, Predicate<Display> lockFilter);
}
