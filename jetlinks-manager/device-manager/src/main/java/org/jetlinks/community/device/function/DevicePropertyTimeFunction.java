package org.jetlinks.community.device.function;

import org.jetlinks.core.message.DeviceDataManager;
import org.jetlinks.reactor.ql.supports.map.FunctionMapFeature;
import org.jetlinks.reactor.ql.utils.CastUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 在reactorQL中获取设备属性上报时间
 * <pre>{@code
 * select device.property_time.recent(deviceId,'temperature',timestamp) recent from ...
 *
 * select * from ... where device.property_time.recent(deviceId,'temperature',timestamp)  = 'xxx'
 * }</pre>
 *
 * @author zhouhao
 * @since 2.2
 */
@Component
public class DevicePropertyTimeFunction extends FunctionMapFeature {
    public DevicePropertyTimeFunction(DeviceDataManager dataManager) {
        super("device.property_time.recent", 3, 2, flux -> flux
            .collectList()
            .flatMap(args -> {
                if (args.size() < 2) {
                    return Mono.empty();
                }
                String deviceId = String.valueOf(args.get(0));
                String property = String.valueOf(args.get(1));
                long timestamp = args.size() > 2
                    ? CastUtils.castNumber(args.get(2))
                               .longValue()
                    : System.currentTimeMillis();

                return dataManager
                    .getLastProperty(deviceId, property, timestamp)
                    .map(DeviceDataManager.PropertyValue::getTimestamp);
            }));
    }
}
