package unconfined.mod.client.util.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public final class MutableRect2i implements Rect2i {
    private int x;
    private int y;
    private int width;
    private int height;
}
