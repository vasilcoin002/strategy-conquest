module pjvsemproj.pjvsemproj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires com.google.gson;

    opens pjvsemproj to javafx.fxml;
    exports pjvsemproj;
    exports pjvsemproj.dto;
    exports pjvsemproj.models.entities;
    exports pjvsemproj.models.entities.cities;
    exports pjvsemproj.models.entities.troopUnits;
    exports pjvsemproj.models.game;
    exports pjvsemproj.models.game.players;
    exports pjvsemproj.models.game.maps;
    exports pjvsemproj.models.services;
    exports pjvsemproj.models.managers;
    exports pjvsemproj.models.managers.utils;
    exports pjvsemproj.server;
    exports pjvsemproj.controllers;
    exports pjvsemproj.views;
    exports pjvsemproj.views.game;
}