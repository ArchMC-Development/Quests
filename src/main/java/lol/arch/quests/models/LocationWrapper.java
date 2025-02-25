package lol.arch.quests.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class LocationWrapper {
    private final Location location;

    @Override
    public int hashCode() {
        return String.format(
                        "%s %s %s %s",
                        location.getWorld().getName(), location.getX(), location.getY(), location.getZ())
                .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Location other = ((LocationWrapper) obj).getLocation();
        return location.getX() == other.getX()
                && location.getY() == other.getY()
                && location.getZ() == other.getZ()
                && location.getWorld().getName().equals(other.getWorld().getName());
    }
}
