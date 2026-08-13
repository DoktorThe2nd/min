local M = {}

function M.instanceOf(obj, class)
    return class:isAssignableFrom(obj:getClass())
end

function M.instanceOfOnly(obj, class)
    return class:isInstance(obj)
end

function M.javaNew(class, ...)
    return luajava.new(class, ...)
end

return M