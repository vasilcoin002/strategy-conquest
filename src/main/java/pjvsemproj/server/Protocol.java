package pjvsemproj.server;

public enum Protocol {
    LOGIN,
    READY,
    QUIT,

    MOVE,
    ATTACK,
    BUY_UNIT,
    UPGRADE_CITY,
    END_TURN,

    OK,
    ERROR,

    GAME_STARTED,
    TURN_STARTED,
    UNIT_MOVED,
    UNIT_ATTACKED,
    UNIT_DIED,
    UNIT_BOUGHT,
    CITY_UPGRADED,
    GOLD_UPDATED,
    GAME_OVER
}