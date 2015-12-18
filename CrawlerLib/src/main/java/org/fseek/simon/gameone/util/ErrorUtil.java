/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.fseek.simon.gameone.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.fseek.simon.gameone.parse.MediaOfflineException;
import org.fseek.simon.gameone.parse.ParseException;

public class ErrorUtil {
    public static ParseException parseError(String message, Throwable cause) {
        return new ParseException(message, cause);
    }

    public static ParseException parseError(String message) {
        return new ParseException(message);
    }

    public static ParseException parseError(Throwable cause) {
        return new ParseException(cause);
    }

    public static MediaOfflineException offlineError(String message, Throwable cause) {
        return new MediaOfflineException(message, cause);
    }

    public static MediaOfflineException offlineError(String message) {
        return new MediaOfflineException(message);
    }

    public static MediaOfflineException offlineError(Throwable cause) {
        return new MediaOfflineException(cause);
    }

    public static <T> Callable<T> rethrow(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                return sneakyThrow(e);
            }
        };
    }

    public static <T, R> Function<T, R> rethrow(FunctionException<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                return sneakyThrow(e);
            }
        };
    }

    public static <T> Consumer<T> rethrow(ConsumerException<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                sneakyThrow(e);
            }
        };
    }

    public static <T> Supplier<T> rethrow(SupplierException<T> function) {
        return () -> {
            try {
                return function.get();
            } catch (Exception e) {
                return sneakyThrow(e);
            }
        };
    }

    public static <T> T sneakyThrow(Throwable e) {
        return ErrorUtil.<RuntimeException, T> sneakyThrow0(e);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, T> T sneakyThrow0(Throwable t) throws E {
        throw (E) t;
    }

    @FunctionalInterface
    public interface FunctionException<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface SupplierException<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface RunnableException {
        void accept() throws Exception;
    }

    @FunctionalInterface
    public interface ConsumerException<T> {
        void accept(T t) throws Exception;
    }
}
