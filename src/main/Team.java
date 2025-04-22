package main;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Team {
    Raimon,
    Occult,
    Wild,
    @JsonProperty("Brainwashing")
    Brain,
    Otaku,
    @JsonProperty("Royal Academy")
    Royal_Academy,
    Shuriken,
    Farm,
    Kirkwood,
    Zeus,
    @JsonProperty("Raimon Old Boys")
    Raimon_Old_Boys,
    @JsonProperty("Street Sally\u0027s")
    Street_Sallys,
    @JsonProperty("Inazuma KFC")
    Inazuma_KFC,
    Umbrella,
    @JsonProperty("Connection Map")
    Conection_Map,
    Scouting
}
