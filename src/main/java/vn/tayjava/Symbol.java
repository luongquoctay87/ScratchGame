package vn.tayjava;

import com.fasterxml.jackson.annotation.JsonProperty;

class Symbol {
    @JsonProperty("reward_multiplier")
    private int rewardMultiplier;
    private String type;
    private int extra;

    public int getRewardMultiplier() {
        return rewardMultiplier;
    }

    public int getExtra() {
        return extra;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "rewardMultiplier=" + rewardMultiplier +
                ", type='" + type + '\'' +
                ", extra=" + extra +
                '}';
    }
}
