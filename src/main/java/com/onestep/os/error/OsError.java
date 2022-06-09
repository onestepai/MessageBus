//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.onestep.os.error;

public enum OsError implements Error {
    OS_SUCCESS(0L, "success"),
    OS_FAILURE_INVALID_ARG(2147483649L, "InvalidArgumentException"),
    OS_FAILURE_TIMEOUT(2147483650L, "TimeoutException"),
    OS_FAILURE_INVALID_PERMISSION(2147483651L, "InvalidPermissionException"),
    OS_FAILURE_DB_ERROR(2147483652L, "DataException"),
    OS_FAILURE_DOES_NOT_EXIST_DATA(2147483653L, "DataNotExistException"),
    OS_FAILURE_DUPLICATE_DATA(2147483654L, "DataDuplicateException"),
    OS_FAILURE_ERROR_IN_PARTIAL(2147483655L, "PartialErrorException"),
    OS_FAILURE_NETWORK(2147483656L, "ConnectionException"),
    OS_FAILURE_INTERNAL(2147483657L, "InternalException"),
    OS_FAILURE_HTTP_POST(2147483658L, "HTTP Post Exception"),
    OS_FAILURE_HTTP_GET(2147483659L, "HTTP Get Exception"),
    OS_FAILURE_HTTP_PUT(2147483660L, "HTTP Put Exception"),
    OS_FAILURE_UPLOAD_FILE_BY_FILE(2147483661L, "upload file by file exception"),
    OS_FAILURE_SYNC_FILE(2147483662L, "sync file exception"),
    OS_FAILURE_SYNC_CONTENT_STRING(2147483663L, "sync content string exception"),
    OS_FAILURE_WEB_LOG_POINT_CUT(2147483664L, "web log point cut exception"),
    OS_FAILURE_JSON_CONVERT(2147483665L, "json convert exception"),
    OS_FAILURE_DEPENDENCY(2147483666L, "DependencyException");

    private Long errorCode;
    private String description;

    private OsError(Long errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public String getError() {
        return this.description;
    }

    public Long getErrorCode() {
        return this.errorCode;
    }
}
