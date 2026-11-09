#!/bin/bash
# Static check: every mixin injection point must exist in the real Minecraft 26.2 bytecode.
CP=$(find .gradle/loom-cache/minecraftMaven -name '*.jar' ! -name '*sources*' 2>/dev/null | tr '\n' ':')
echo "classpath jars:"; echo "$CP" | tr ':' '\n'
jp() { javap -c -p -cp "$CP" "$@" 2>&1; }
# prints lines of the given method body that contain a fragment
body() { # class methodRegex fragment
  jp "$1" | awk -v m="$2" '/^  [a-zA-Z].*\(.*\).*;$/ {p=($0 ~ m)} p' | grep -F "$3"
}
sig() { jp -cp "$CP" "$1" | grep -E "$2"; }
echo; echo "### LocalPlayer.aiStep -> goDownInWater"; body net.minecraft.client.player.LocalPlayer ' aiStep[(]' 'goDownInWater'
echo; echo "### LocalPlayer.sendIsSprintingIfNeeded -> packet ctor"; body net.minecraft.client.player.LocalPlayer 'sendIsSprintingIfNeeded' 'ServerboundPlayerCommandPacket."<init>"'
echo; echo "### Entity.updateSwimming -> isSprinting"; body net.minecraft.world.entity.Entity ' updateSwimming[(]' 'isSprinting'
echo; echo "### LivingEntity.aiStep -> jumpInLiquid"; body net.minecraft.world.entity.LivingEntity ' aiStep[(]' 'jumpInLiquid'
echo; echo "### Player.travel -> setDeltaMovement"; body net.minecraft.world.entity.player.Player ' travel[(]' 'setDeltaMovement'
echo; echo "### LivingEntity members used by @Shadow / @Inject"
jp net.minecraft.world.entity.LivingEntity | grep -E ' (getWaterSlowDown|getSpeed|hasEffect|onClimbable|getAttributeValue|jumpOutOfFluid|travelInWater)\('
echo; echo "### LocalPlayer.isUnderWater"; jp net.minecraft.client.player.LocalPlayer | grep -E ' isUnderWater\('
echo; echo "### AvatarRenderer.setupRotations"; jp net.minecraft.client.renderer.entity.player.AvatarRenderer | grep -E ' setupRotations\('
echo; echo "### AvatarRenderState swimming field"; jp net.minecraft.client.renderer.entity.state.AvatarRenderState | grep -E 'isVisuallySwimming'
echo; echo "### Player ledge-related methods"; jp net.minecraft.world.entity.player.Player | grep -i -E 'BackOff|Edge|StayingOn|clipAt'
echo; echo "### Player.maybeBackOffFromEdge body (calls)"; body net.minecraft.world.entity.player.Player 'maybeBackOffFromEdge' 'invoke'
echo; echo "### Entity.isStayingOnGroundSurface"; jp net.minecraft.world.entity.Entity | grep -E 'isStayingOnGroundSurface|isShiftKeyDown'
echo; echo "### Util.OS members"; jp 'net.minecraft.Util$OS' | grep -E 'open|Uri|Url|URI'
echo; echo "### Util.getPlatform"; jp net.minecraft.Util | grep -E 'getPlatform'
echo; echo "### InputConstants key O"; jp com.mojang.blaze3d.platform.InputConstants | grep -E ' KEY_O;| KEY_O =|UNKNOWN'
echo; echo "### PoseStack.mulPose"; jp com.mojang.blaze3d.vertex.PoseStack | grep -E 'mulPose'
echo; echo "### Minecraft screen methods"; jp net.minecraft.client.Minecraft | grep -E 'Screen' | head -10
echo; echo "### ConfirmLinkScreen"; jp net.minecraft.client.gui.screens.ConfirmLinkScreen | grep -E 'public|static' | head -12

echo; echo "### net.minecraft.util.Util OS"; jp 'net.minecraft.util.Util$OS' | head -40
echo; echo "### ConfirmLinkScreen statics"; jp net.minecraft.client.gui.screens.ConfirmLinkScreen | grep -E 'static' | grep -v 'invoke' | head
echo; echo "### classes named *OS* or opening URIs"; unzip -l $(echo "$CP" | cut -d: -f1) | awk '{print $4}' | grep -E 'util/(Util|OpenUri|Platform|PlatformUtil)|/Os|/OS' | head

echo; echo "### classes containing openUri/openUrl"
python3 - "$(echo "$CP" | cut -d: -f1)" <<'PY'
import sys, zipfile
z = zipfile.ZipFile(sys.argv[1])
hits = []
for n in z.namelist():
    if n.endswith('.class'):
        b = z.read(n)
        if b'openUri' in b or b'openUrl' in b or b'openURI' in b:
            hits.append(n)
print("\n".join(hits[:40]))
PY
