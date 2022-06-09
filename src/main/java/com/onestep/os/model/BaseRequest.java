//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.onestep.os.model;

public class BaseRequest {
    private String requestId;

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseRequest)) {
            return false;
        } else {
            BaseRequest other = (BaseRequest)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$requestId = this.getRequestId();
                Object other$requestId = other.getRequestId();
                if (this$requestId == null) {
                    if (other$requestId != null) {
                        return false;
                    }
                } else if (!this$requestId.equals(other$requestId)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseRequest;
    }

    public int hashCode() {
        Object $requestId = this.getRequestId();
        return  1 * 59 + ($requestId == null ? 43 : $requestId.hashCode());
    }

    public String toString() {
        return "BaseRequest(requestId=" + this.getRequestId() + ")";
    }

    public BaseRequest() {
    }
}
