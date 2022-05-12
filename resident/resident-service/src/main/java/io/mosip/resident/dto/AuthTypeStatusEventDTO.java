package io.mosip.resident.dto;

import io.mosip.resident.constant.EventType;

import java.time.LocalDateTime;
import java.util.List;

public class AuthTypeStatusEventDTO {
    private EventType eventType;
    private String saltedIdHash;
    private String tokenId;
    private List<AuthtypeStatus> authTypeStatusList;
    private LocalDateTime expiryTimestamp;
    private Integer transactionLimit;

    public EventType getEventType() {
        return this.eventType;
    }

    public String getSaltedIdHash() {
        return this.saltedIdHash;
    }

    public String getTokenId() {
        return this.tokenId;
    }

    public List<AuthtypeStatus> getAuthTypeStatusList() {
        return this.authTypeStatusList;
    }

    public LocalDateTime getExpiryTimestamp() {
        return this.expiryTimestamp;
    }

    public Integer getTransactionLimit() {
        return this.transactionLimit;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setSaltedIdHash(String saltedIdHash) {
        this.saltedIdHash = saltedIdHash;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public void setAuthTypeStatusList(List<AuthtypeStatus> authTypeStatusList) {
        this.authTypeStatusList = authTypeStatusList;
    }

    public void setExpiryTimestamp(LocalDateTime expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    public void setTransactionLimit(Integer transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AuthTypeStatusEventDTO)) {
            return false;
        } else {
            AuthTypeStatusEventDTO other = (AuthTypeStatusEventDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$eventType = this.getEventType();
                Object other$eventType = other.getEventType();
                if (this$eventType == null) {
                    if (other$eventType != null) {
                        return false;
                    }
                } else if (!this$eventType.equals(other$eventType)) {
                    return false;
                }

                Object this$saltedIdHash = this.getSaltedIdHash();
                Object other$saltedIdHash = other.getSaltedIdHash();
                if (this$saltedIdHash == null) {
                    if (other$saltedIdHash != null) {
                        return false;
                    }
                } else if (!this$saltedIdHash.equals(other$saltedIdHash)) {
                    return false;
                }

                Object this$tokenId = this.getTokenId();
                Object other$tokenId = other.getTokenId();
                if (this$tokenId == null) {
                    if (other$tokenId != null) {
                        return false;
                    }
                } else if (!this$tokenId.equals(other$tokenId)) {
                    return false;
                }

                label62: {
                    Object this$authTypeStatusList = this.getAuthTypeStatusList();
                    Object other$authTypeStatusList = other.getAuthTypeStatusList();
                    if (this$authTypeStatusList == null) {
                        if (other$authTypeStatusList == null) {
                            break label62;
                        }
                    } else if (this$authTypeStatusList.equals(other$authTypeStatusList)) {
                        break label62;
                    }

                    return false;
                }

                label55: {
                    Object this$expiryTimestamp = this.getExpiryTimestamp();
                    Object other$expiryTimestamp = other.getExpiryTimestamp();
                    if (this$expiryTimestamp == null) {
                        if (other$expiryTimestamp == null) {
                            break label55;
                        }
                    } else if (this$expiryTimestamp.equals(other$expiryTimestamp)) {
                        break label55;
                    }

                    return false;
                }

                Object this$transactionLimit = this.getTransactionLimit();
                Object other$transactionLimit = other.getTransactionLimit();
                if (this$transactionLimit == null) {
                    if (other$transactionLimit != null) {
                        return false;
                    }
                } else if (!this$transactionLimit.equals(other$transactionLimit)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof AuthTypeStatusEventDTO;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        Object $saltedIdHash = this.getSaltedIdHash();
        result = result * 59 + ($saltedIdHash == null ? 43 : $saltedIdHash.hashCode());
        Object $tokenId = this.getTokenId();
        result = result * 59 + ($tokenId == null ? 43 : $tokenId.hashCode());
        Object $authTypeStatusList = this.getAuthTypeStatusList();
        result = result * 59 + ($authTypeStatusList == null ? 43 : $authTypeStatusList.hashCode());
        Object $expiryTimestamp = this.getExpiryTimestamp();
        result = result * 59 + ($expiryTimestamp == null ? 43 : $expiryTimestamp.hashCode());
        Object $transactionLimit = this.getTransactionLimit();
        result = result * 59 + ($transactionLimit == null ? 43 : $transactionLimit.hashCode());
        return result;
    }

    public String toString() {
        EventType var10000 = this.getEventType();
        return "AuthTypeStatusEventDTO(eventType=" + var10000 + ", saltedIdHash=" + this.getSaltedIdHash() + ", tokenId=" + this.getTokenId() + ", authTypeStatusList=" + this.getAuthTypeStatusList() + ", expiryTimestamp=" + this.getExpiryTimestamp() + ", transactionLimit=" + this.getTransactionLimit() + ")";
    }

    public AuthTypeStatusEventDTO() {
    }

    public AuthTypeStatusEventDTO(EventType eventType, String saltedIdHash, String tokenId, List<AuthtypeStatus> authTypeStatusList, LocalDateTime expiryTimestamp, Integer transactionLimit) {
        this.eventType = eventType;
        this.saltedIdHash = saltedIdHash;
        this.tokenId = tokenId;
        this.authTypeStatusList = authTypeStatusList;
        this.expiryTimestamp = expiryTimestamp;
        this.transactionLimit = transactionLimit;
    }
}
