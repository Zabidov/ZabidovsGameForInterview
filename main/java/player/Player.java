package player;

import enums.ChooseType;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Player {
    private final String name;
    @Setter
    private ChooseType choice;

    public Player(String name) {
        this.name = name;
    }
}
