var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
import { IonicNativePlugin, cordova } from '@ionic-native/core';
var ImapOriginal = /** @class */ (function (_super) {
    __extends(ImapOriginal, _super);
    function ImapOriginal() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    ImapOriginal.prototype.connect = function (clientData) { return cordova(this, "connect", {}, arguments); };
    ImapOriginal.prototype.disconnect = function () { return cordova(this, "disconnect", {}, arguments); };
    ImapOriginal.prototype.listMailFolders = function () { return cordova(this, "listMailFolders", {}, arguments); };
    ImapOriginal.prototype.getMessageCountByFolderName = function (folderName) { return cordova(this, "getMessageCountByFolderName", {}, arguments); };
    ImapOriginal.prototype.listMessagesByDatePeriod = function (folderName, dateInMilliseconds, comparison) { return cordova(this, "listMessagesByDatePeriod", {}, arguments); };
    ImapOriginal.prototype.listMessagesNumberByDatePeriod = function (folderName, dateInMilliseconds, comparison) { return cordova(this, "listMessagesNumberByDatePeriod", {}, arguments); };
    ImapOriginal.prototype.getFullMessageData = function (folderName, messageNumber) { return cordova(this, "getFullMessageData", {}, arguments); };
    ImapOriginal.pluginName = "Imap";
    ImapOriginal.plugin = "cordova-plugin-imap";
    ImapOriginal.pluginRef = "imap";
    ImapOriginal.repo = "";
    ImapOriginal.install = "";
    ImapOriginal.installVariables = [];
    ImapOriginal.platforms = ["Android"];
    return ImapOriginal;
}(IonicNativePlugin));
var Imap = new ImapOriginal();
export { Imap };
// ReceivedDateTerm.GE
export var Comparison;
(function (Comparison) {
    Comparison[Comparison["LE"] = 1] = "LE";
    Comparison[Comparison["LT"] = 2] = "LT";
    Comparison[Comparison["EQ"] = 3] = "EQ";
    Comparison[Comparison["NE"] = 4] = "NE";
    Comparison[Comparison["GT"] = 5] = "GT";
    Comparison[Comparison["GE"] = 6] = "GE";
})(Comparison || (Comparison = {}));
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiaW5kZXguanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi9zcmMvQGlvbmljLW5hdGl2ZS9wbHVnaW5zL2ltYXAvaW5kZXgudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7OztBQVlBLE9BQU8sOEJBT04sTUFBTSxvQkFBb0IsQ0FBQzs7SUFrQ0Ysd0JBQWlCOzs7O0lBVXpDLHNCQUFPLGFBQUMsVUFBa0I7SUFLMUIseUJBQVU7SUFLViw4QkFBZTtJQUtmLDBDQUEyQixhQUFDLFVBQWtCO0lBSzlDLHVDQUF3QixhQUFDLFVBQWtCLEVBQUUsa0JBQTBCLEVBQUUsVUFBc0I7SUFLL0YsNkNBQThCLGFBQUMsVUFBa0IsRUFBRSxrQkFBMEIsRUFBRSxVQUFzQjtJQUtyRyxpQ0FBa0IsYUFBQyxVQUFrQixFQUFFLGFBQXFCOzs7Ozs7OztlQTdGOUQ7RUFxRDBCLGlCQUFpQjtTQUE5QixJQUFJO0FBK0VqQixzQkFBc0I7QUFDdEIsTUFBTSxDQUFOLElBQVksVUFPWDtBQVBELFdBQVksVUFBVTtJQUNwQix1Q0FBTSxDQUFBO0lBQ04sdUNBQU0sQ0FBQTtJQUNOLHVDQUFNLENBQUE7SUFDTix1Q0FBTSxDQUFBO0lBQ04sdUNBQU0sQ0FBQTtJQUNOLHVDQUFNLENBQUE7QUFDUixDQUFDLEVBUFcsVUFBVSxLQUFWLFVBQVUsUUFPckIiLCJzb3VyY2VzQ29udGVudCI6WyIvKipcbiAqIFRoaXMgaXMgYSB0ZW1wbGF0ZSBmb3IgbmV3IHBsdWdpbiB3cmFwcGVyc1xuICpcbiAqIFRPRE86XG4gKiAtIEFkZC9DaGFuZ2UgaW5mb3JtYXRpb24gYmVsb3dcbiAqIC0gRG9jdW1lbnQgdXNhZ2UgKGltcG9ydGluZywgZXhlY3V0aW5nIG1haW4gZnVuY3Rpb25hbGl0eSlcbiAqIC0gUmVtb3ZlIGFueSBpbXBvcnRzIHRoYXQgeW91IGFyZSBub3QgdXNpbmdcbiAqIC0gUmVtb3ZlIGFsbCB0aGUgY29tbWVudHMgaW5jbHVkZWQgaW4gdGhpcyB0ZW1wbGF0ZSwgRVhDRVBUIHRoZSBAUGx1Z2luIHdyYXBwZXIgZG9jcyBhbmQgYW55IG90aGVyIGRvY3MgeW91IGFkZGVkXG4gKiAtIFJlbW92ZSB0aGlzIG5vdGVcbiAqXG4gKi9cbmltcG9ydCB7SW5qZWN0YWJsZX0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQge1xuICBQbHVnaW4sXG4gIENvcmRvdmEsXG4gIENvcmRvdmFQcm9wZXJ0eSxcbiAgQ29yZG92YUluc3RhbmNlLFxuICBJbnN0YW5jZVByb3BlcnR5LFxuICBJb25pY05hdGl2ZVBsdWdpblxufSBmcm9tICdAaW9uaWMtbmF0aXZlL2NvcmUnO1xuaW1wb3J0IHtPYnNlcnZhYmxlfSBmcm9tICdyeGpzJztcblxuLyoqXG4gKiBAbmFtZSBJbWFwXG4gKiBAZGVzY3JpcHRpb25cbiAqIFRoaXMgcGx1Z2luIGRvZXMgc29tZXRoaW5nXG4gKlxuICogQHVzYWdlXG4gKiBgYGB0eXBlc2NyaXB0XG4gKiBpbXBvcnQgeyBJbWFwIH0gZnJvbSAnQGlvbmljLW5hdGl2ZS9pbWFwL25neCc7XG4gKlxuICpcbiAqIGNvbnN0cnVjdG9yKHByaXZhdGUgaW1hcDogSW1hcCkgeyB9XG4gKlxuICogLi4uXG4gKlxuICpcbiAqIHRoaXMuaW1hcC5mdW5jdGlvbk5hbWUoJ0hlbGxvJywgMTIzKVxuICogICAudGhlbigocmVzOiBhbnkpID0+IGNvbnNvbGUubG9nKHJlcykpXG4gKiAgIC5jYXRjaCgoZXJyb3I6IGFueSkgPT4gY29uc29sZS5lcnJvcihlcnJvcikpO1xuICpcbiAqIGBgYFxuICovXG5AUGx1Z2luKHtcbiAgcGx1Z2luTmFtZTogJ0ltYXAnLFxuICBwbHVnaW46ICdjb3Jkb3ZhLXBsdWdpbi1pbWFwJywgLy8gbnBtIHBhY2thZ2UgbmFtZSwgZXhhbXBsZTogY29yZG92YS1wbHVnaW4tY2FtZXJhXG4gIHBsdWdpblJlZjogJ2ltYXAnLCAvLyB0aGUgdmFyaWFibGUgcmVmZXJlbmNlIHRvIGNhbGwgdGhlIHBsdWdpbiwgZXhhbXBsZTogbmF2aWdhdG9yLmdlb2xvY2F0aW9uXG4gIHJlcG86ICcnLCAvLyB0aGUgZ2l0aHViIHJlcG9zaXRvcnkgVVJMIGZvciB0aGUgcGx1Z2luXG4gIGluc3RhbGw6ICcnLCAvLyBPUFRJT05BTCBpbnN0YWxsIGNvbW1hbmQsIGluIGNhc2UgdGhlIHBsdWdpbiByZXF1aXJlcyB2YXJpYWJsZXNcbiAgaW5zdGFsbFZhcmlhYmxlczogW10sIC8vIE9QVElPTkFMIHRoZSBwbHVnaW4gcmVxdWlyZXMgdmFyaWFibGVzXG4gIHBsYXRmb3JtczogWydBbmRyb2lkJ10gLy8gQXJyYXkgb2YgcGxhdGZvcm1zIHN1cHBvcnRlZCwgZXhhbXBsZTogWydBbmRyb2lkJywgJ2lPUyddXG59KVxuQEluamVjdGFibGUoKVxuZXhwb3J0IGNsYXNzIEltYXAgZXh0ZW5kcyBJb25pY05hdGl2ZVBsdWdpbiB7XG5cbiAgLyoqXG4gICAqIFRoaXMgZnVuY3Rpb24gZG9lcyBzb21ldGhpbmdcbiAgICogQHBhcmFtIGFyZzEge3N0cmluZ30gU29tZSBwYXJhbSB0byBjb25maWd1cmUgc29tZXRoaW5nXG4gICAqIEBwYXJhbSBhcmcyIHtudW1iZXJ9IEFub3RoZXIgcGFyYW0gdG8gY29uZmlndXJlIHNvbWV0aGluZ1xuICAgKiBAcmV0dXJuIHtQcm9taXNlPGFueT59IFJldHVybnMgYSBwcm9taXNlIHRoYXQgcmVzb2x2ZXMgd2hlbiBzb21ldGhpbmcgaGFwcGVuc1xuICAgKi9cblxuICBAQ29yZG92YSgpXG4gIGNvbm5lY3QoY2xpZW50RGF0YTogQ29uZmlnKTogUHJvbWlzZTxzdHJpbmc+IHtcbiAgICByZXR1cm47XG4gIH1cblxuICBAQ29yZG92YSgpXG4gIGRpc2Nvbm5lY3QoKTogUHJvbWlzZTxib29sZWFuPiB7XG4gICAgcmV0dXJuO1xuICB9XG5cbiAgQENvcmRvdmEoKVxuICBsaXN0TWFpbEZvbGRlcnMoKTogUHJvbWlzZTxzdHJpbmdbXT4ge1xuICAgIHJldHVybjtcbiAgfVxuXG4gIEBDb3Jkb3ZhKClcbiAgZ2V0TWVzc2FnZUNvdW50QnlGb2xkZXJOYW1lKGZvbGRlck5hbWU6IHN0cmluZyk6IFByb21pc2U8bnVtYmVyPiB7XG4gICAgcmV0dXJuO1xuICB9XG5cbiAgQENvcmRvdmEoKVxuICBsaXN0TWVzc2FnZXNCeURhdGVQZXJpb2QoZm9sZGVyTmFtZTogc3RyaW5nLCBkYXRlSW5NaWxsaXNlY29uZHM6IHN0cmluZywgY29tcGFyaXNvbjogQ29tcGFyaXNvbik6IFByb21pc2U8bnVtYmVyW10+IHtcbiAgICByZXR1cm47XG4gIH1cblxuICBAQ29yZG92YSgpXG4gIGxpc3RNZXNzYWdlc051bWJlckJ5RGF0ZVBlcmlvZChmb2xkZXJOYW1lOiBzdHJpbmcsIGRhdGVJbk1pbGxpc2Vjb25kczogc3RyaW5nLCBjb21wYXJpc29uOiBDb21wYXJpc29uKTogUHJvbWlzZTxhbnk+IHtcbiAgICByZXR1cm47XG4gIH1cblxuICBAQ29yZG92YSgpXG4gIGdldEZ1bGxNZXNzYWdlRGF0YShmb2xkZXJOYW1lOiBzdHJpbmcsIG1lc3NhZ2VOdW1iZXI6IG51bWJlcik6IFByb21pc2U8TWVzc2FnZT4ge1xuICAgIHJldHVybjtcbiAgfVxufVxuXG5leHBvcnQgaW50ZXJmYWNlIENvbmZpZyB7XG4gIGhvc3Q6IHN0cmluZztcbiAgdXNlcjogc3RyaW5nO1xuICBwYXNzd29yZDogc3RyaW5nO1xufVxuXG5leHBvcnQgaW50ZXJmYWNlIE1lc3NhZ2Uge1xuICBtZXNzYWdlTnVtYmVyOiBudW1iZXI7XG4gIGZvbGRlcjogc3RyaW5nO1xuICBmcm9tOiBzdHJpbmdbXTtcbiAgYWxsUmVjaXBpZW50czogc3RyaW5nW107XG4gIHRvUmVjaXBpZW50czogc3RyaW5nW107XG4gIGNjUmVjaXBpZW50czogc3RyaW5nW107XG4gIGJjY1JlY2lwaWVudHM6IHN0cmluZ1tdO1xuICByZXBseVRvOiBzdHJpbmdbXTtcbiAgc2VudERhdGU6IHN0cmluZztcbiAgcmVjZWl2ZWREYXRlOiBzdHJpbmc7XG4gIHN1YmplY3Q6IHN0cmluZztcbiAgZGVzY3JpcHRpb246IHN0cmluZztcbiAgZmlsZU5hbWU6IHN0cmluZztcbiAgZGlzcG9zaXRpb246IHN0cmluZztcbiAgZmxhZ3M6IHN0cmluZztcbiAgbGluZUNvdW50OiBudW1iZXI7XG4gIGFsbE1lc3NhZ2VIZWFkZXJzOiBvYmplY3Q7XG4gIGNvbnRlbnRUeXBlOiBzdHJpbmc7XG4gIGJvZHlDb250ZW50OiBDb250ZW50W107XG4gIHNpemU6IG51bWJlcjtcbn1cblxuZXhwb3J0IGludGVyZmFjZSBDb250ZW50IHtcbiAgdHlwZTogc3RyaW5nO1xuICBjb250ZW50OiBzdHJpbmc7XG59XG5cbi8vIFJlY2VpdmVkRGF0ZVRlcm0uR0VcbmV4cG9ydCBlbnVtIENvbXBhcmlzb24ge1xuICBMRSA9IDEsXG4gIExUID0gMixcbiAgRVEgPSAzLFxuICBORSA9IDQsXG4gIEdUID0gNSxcbiAgR0UgPSA2LFxufVxuIl19