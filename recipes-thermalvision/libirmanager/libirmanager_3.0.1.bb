SUMMARY = "Optris PI thermal imaging software"
DESCRIPTION = "This software allows connecting with Optris PI thermal imaging camera"
HOMEPAGE = "http://www.optris.com/optris-pi-sdk"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${WORKDIR}/freebsd.txt;md5=2c9cef41cd861f6c9c7d0ed45926ed2c"

DEPENDS += "udev"

# deb are compressed with xz
do_unpack[depends] += "xz-native:do_populate_sysroot"

SRC_URI= "http://documentation.evocortex.com/en/freebsd.txt;name=license"
SRC_URI[license.md5sum] = "2c9cef41cd861f6c9c7d0ed45926ed2c"
SRC_URI[license.sha256sum] = "cafc03c1cdc772ed623561872b2d32d333257e3ef41bef4cb9e9c4749a1056b0"

python () {
    target_arch = d.getVar("TARGET_ARCH", True)
    if target_arch=="i386":
        ir_arch = "i386"
    elif target_arch=="i586":
        ir_arch = "atom"
    elif target_arch=="x86_64":
        ir_arch = "amd64"
    elif target_arch=="arm" and bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', True, False, d):
        ir_arch = "armhf"
    elif target_arch=="arm":
        ir_arch = "armel"
    elif target_arch=="aarch64":
        ir_arch = "arm64-libusb"
    else:
        bb.fatal("unknown target_arch %s" % target_arch)

    md5sums = {
        "amd64" : "c11d051f5e86ef6deb3815d298454610",
        "arm64" : "696ee831d377f31bbe0e5a68226e56ce",
        "armel" : "e6b39ec856cef247138fb62ea3ba5066",
        "armhf" : "ab996f31a0ec272518c26616095b23b2",
        "atom"  : "ccbe33b2d2c6960166dfd10924933415",
        "i386"  : "58bfaaba82d69b9e41be771837edfc36"
        }

    sha256sums = {
        "amd64" : "cfc4ee86a34bcb2c0795ec7aed11352b55477078291fb0d687d826d9c14e776e",
        "arm64" : "b7e8133e82d86557d8a4d59603accc162e6a37b1587526cfa9c9e22437b086f7",
        "armel" : "82379e4837b6929e30353871f41f847dfad338038e4acd638b2a46d352f7eb04",
        "armhf" : "d04b5c3effac17d396582865bb39c59be4583fe0c13ad5ba8332636411893e1f",
        "atom"  : "609993173d5f4f54936d157d1f236ec574130041b6a7a5d4ae700a1b3964ff89",
        "i386"  : "04d9d6080468713ac004035f2c0cac0970213b17ba4144f3c0d121036324f1de",
        }

    d.appendVar("SRC_URI", " http://ftp.evocortex.com/libirimager-${PV}-%s.deb " % ir_arch)
    d.setVarFlag("SRC_URI", "md5sum", md5sums[ir_arch])
    d.setVarFlag("SRC_URI", "sha256sum", sha256sums[ir_arch])
}

do_compile() {
    mkdir -p ${S}/usr
    cp -r ${WORKDIR}/usr ${S}
    cd ${S}/usr/lib

    mv libircore_nonshared.a libircore.a
    mv libirdirectbinding_nonshared.a libirdirectbinding.a
    mv libirdirectsdk_nonshared.a libirdirectsdk.a
    mv libirimageprocessing_nonshared.a libirimageprocessing.a
    mv libirimager_nonshared.a libirimager.a

    mv libircore.so libircore.so.${PV}
    mv libirdirectbinding.so libirdirectbinding.so.${PV}
    mv libirdirectsdk.so libirdirectsdk.so.${PV}
    mv libirimageprocessing.so libirimageprocessing.so.${PV}
    mv libirimager.so libirimager.so.${PV}
}

do_install() {
   install -d ${D}/usr/lib
   oe_soinstall ${S}/usr/lib/libircore.so.${PV} ${D}${libdir}
   oe_soinstall ${S}/usr/lib/libirdirectbinding.so.${PV} ${D}${libdir}
   oe_soinstall ${S}/usr/lib/libirdirectsdk.so.${PV} ${D}${libdir}
   oe_soinstall ${S}/usr/lib/libirimageprocessing.so.${PV} ${D}${libdir}
   oe_soinstall ${S}/usr/lib/libirimager.so.${PV} ${D}${libdir}
   cp -fr ${S}/usr ${D}
   chown -R root:root ${D}/usr
}

FILES_${PN} += "/usr/share/libirimager/Formats.def /usr/share/libirimager/cali"

INSANE_SKIP_${PN} = "already-stripped"
