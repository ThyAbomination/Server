package server.model.minigames;
 
import server.model.players.Client;
import server.Server;
 
public class RFD {
        private final int[][] WAVES = {{3493},{3494},{3495},{3496},{3491}}; //the waves
        private int[][] coordinates = {{1900,5354,2},{1900,5354,2},{1900,5354,2},{1900,5354,2},{1900,5354,2}};
        //2743 = 360, 2627 = 22, 2630 = 45, 2631 = 90, 2741 = 180
        public void spawnNextWave(Client c) { //spawns next wave
                if (c != null) {
                        if (c.waveId >= WAVES.length) {
                                c.waveId = 0;
                                return;                         
                        }
                        if (c.waveId < 0){
                        return;
                        }
                        int npcAmount = WAVES[c.waveId].length;
                        for (int j = 0; j < npcAmount; j++) {
                                int npc = WAVES[c.waveId][j];
                                int X = coordinates[j][0];
                                int Y = coordinates[j][1];
                                int H = c.heightLevel;
                                int hp = getHp(npc);
                                int max = getMax(npc);
                                int atk = getAtk(npc);
                                int def = getDef(npc);
                                Server.npcHandler.spawnNpc(c, npc, X, Y, H, 0, hp, max, atk, def, true, true);                          
                        }
                        c.RFDToKill = npcAmount; //amount to kill left
                        c.RFDKilled = 0; //an int'ish thing that controls the monsters youve killed
                }
        }
        
        public int getHp(int npc) {
                switch (npc) { //after a switch, you add codes that use case
                        case 3493:
                        return 150;
                        case 3494:
                        return 150;
                        case 3495:
                        return 150;
                        case 3496:
                        return 150;
                        case 3491: 
                        return 80;              
                }
                return 100;
        }
        
        public int getMax(int npc) {
                switch (npc) { //after a switch, you add codes that use case
                        case 3493:
                        return 9;
                        case 3494:
                        return 12;
                        case 3495:
                        return 15;
                        case 3496:
                        return 13;
                        case 3491:
                        return 16;              
                }
                return 5;
        }
        
        public int getAtk(int npc) {
                switch (npc) { //after a switch, you add codes that use case
                        case 3493:
                        return 225;
                        case 3494:
                        return 250;
                        case 3495:
                        return 300;
                        case 3496:
                        return 329;
                        case 3491: 
                        return 400;             
                }
                return 100;
        }
        
        public int getDef(int npc) {
                switch (npc) { //after a switch, you add codes that use case
                        case 3493:
                        return 300;
                        case 3494:
                        return 350;
                        case 3495:
                        return 400;
                        case 3496:
                        return 520;
                        case 3491:
                        return 600;             
                }
                return 100;
        }
        
 
}