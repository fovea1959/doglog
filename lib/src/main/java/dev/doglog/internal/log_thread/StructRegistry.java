// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package dev.doglog.internal.log_thread;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.struct.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Used internally for working with WPILib {@link Struct}s. */
public class StructRegistry {
  private static final String STRUCT_FIELD_NAME = "struct";

  private static final Map<Class<?>, Optional<Struct<?>>> resolvedStructs = new HashMap<>();

  private static DataLog log;

  public static Optional<Struct<?>> getStruct(Class<?> entryClass) {
    Optional<Struct<?>> maybeStruct =
        resolvedStructs.computeIfAbsent(entryClass, key -> getStructRaw(entryClass));

    if (maybeStruct.isPresent() && log != null) {
      log.addSchema(maybeStruct.get());
    }

    return maybeStruct;
  }

  public static void setLog(DataLog log) {
    StructRegistry.log = log;
  }

  private static Optional<Struct<?>> getStructRaw(Class<?> classObj) {
    try {
      var field = classObj.getDeclaredField(STRUCT_FIELD_NAME);
      var resolvedStruct = (Struct<?>) field.get(null);

      return Optional.of(resolvedStruct);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private StructRegistry() {}
}
