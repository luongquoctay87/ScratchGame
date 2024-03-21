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

    public void setRewardMultiplier(int rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
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
