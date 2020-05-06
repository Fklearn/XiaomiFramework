package b.b.a.e;

import java.util.HashMap;

class a extends HashMap<Integer, HashMap<Integer, String>> {
    a() {
        put(1, new HashMap());
        put(2, new HashMap());
        ((HashMap) get(1)).put(1, "fraud_num_state");
        ((HashMap) get(1)).put(2, "agent_num_state");
        ((HashMap) get(1)).put(3, "sell_num_state");
        ((HashMap) get(1)).put(10, "harass_num_state");
        ((HashMap) get(2)).put(1, "fraud_num_state_sim_2");
        ((HashMap) get(2)).put(2, "agent_num_state_sim_2");
        ((HashMap) get(2)).put(3, "sell_num_state_sim_2");
        ((HashMap) get(2)).put(10, "harass_num_state_sim_2");
    }
}
