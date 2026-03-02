package unconfined.util;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.io.DataOutput;

@RequiredArgsConstructor
public class DelegateDataOutput implements DataOutput {
    @Delegate
    private final DataOutput parent;
}
