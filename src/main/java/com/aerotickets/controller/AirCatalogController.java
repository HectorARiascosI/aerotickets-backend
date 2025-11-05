package com.aerotickets.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/catalog")
public class AirCatalogController {

    @GetMapping("/airports/co")
    public ResponseEntity<List<Map<String, String>>> airportsColombia() {
        List<Map<String,String>> list = new ArrayList<>();
        add(list, "BOG", "El Dorado", "Bogotá");
        add(list, "MDE", "J. M. Córdova", "Medellín");
        add(list, "EOH", "Olaya Herrera", "Medellín");
        add(list, "CLO", "Alfonso Bonilla Aragón", "Cali");
        add(list, "CTG", "Rafael Núñez", "Cartagena");
        add(list, "SMR", "Simón Bolívar", "Santa Marta");
        add(list, "BAQ", "Ernesto Cortissoz", "Barranquilla");
        add(list, "PEI", "Matecaña", "Pereira");
        add(list, "CUC", "Camilo Daza", "Cúcuta");
        add(list, "ADZ", "G. O. y G. S. A. Newball", "San Andrés");
        add(list, "BGA", "Palo Negro", "Bucaramanga");
        add(list, "PSO", "Antonio Nariño", "Pasto");
        add(list, "LET", "Alfredo V. Cobo", "Leticia");
        add(list, "AXM", "El Edén", "Armenia");
        add(list, "MZL", "La Nubia", "Manizales");
        add(list, "NVA", "Benito Salas", "Neiva");
        add(list, "PPN", "G. L. Valencia", "Popayán");
        add(list, "UIB", "El Caraño", "Quibdó");
        add(list, "MTR", "Los Garzones", "Montería");
        add(list, "VVC", "Vanguardia", "Villavicencio");
        add(list, "RCH", "Almirante Padilla", "Riohacha");
        return ResponseEntity.ok(list);
    }

    @GetMapping("/airlines/co")
    public ResponseEntity<List<Map<String, String>>> airlinesColombia() {
        List<Map<String,String>> list = new ArrayList<>();
        list.add(Map.of("code","AV","name","Avianca"));
        list.add(Map.of("code","LA","name","LATAM Airlines"));
        list.add(Map.of("code","9R","name","SATENA"));
        list.add(Map.of("code","UL","name","Ultra Air (sim)"));
        list.add(Map.of("code","VH","name","Viva (sim)"));
        return ResponseEntity.ok(list);
    }

    private void add(List<Map<String,String>> list, String iata, String name, String city) {
        list.add(Map.of("iata", iata, "name", name, "city", city, "country", "Colombia"));
    }
}